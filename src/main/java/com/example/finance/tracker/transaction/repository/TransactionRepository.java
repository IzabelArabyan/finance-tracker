package com.example.finance.tracker.transaction.repository;

import com.example.finance.tracker.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    List<Transaction> findByAccountId(Long accountId);

    boolean existsByAccountId(Long accountId);

    boolean existsByCategoryId(Long categoryId);

    @Override
    @EntityGraph(attributePaths = {"account", "category"})
    Optional<Transaction> findById(Long id);

    @Query(value = """
            select t from Transaction t
            join fetch t.account a
            left join fetch t.category c
            where (:accountId is null or a.id = :accountId)
              and (:categoryId is null or c.id = :categoryId)
              and (:from is null or t.date >= :from)
              and (:to is null or t.date <= :to)
            """,
            countQuery = """
                    select count(t) from Transaction t
                    where (:accountId is null or t.account.id = :accountId)
                      and (:categoryId is null or t.category.id = :categoryId)
                      and (:from is null or t.date >= :from)
                      and (:to is null or t.date <= :to)
                    """)
    Page<Transaction> search(@Param("accountId") Long accountId,
                             @Param("categoryId") Long categoryId,
                             @Param("from") LocalDate from,
                             @Param("to") LocalDate to,
                             Pageable pageable);

    @Query("""
            select new com.example.finance.tracker.transaction.repository.CategorySpending(
                c.id, c.name, sum(t.amount))
            from Transaction t
            join t.category c
            where t.type = com.example.finance.tracker.transaction.TransactionType.EXPENSE
              and t.transferId is null
              and t.date >= :from and t.date <= :to
            group by c.id, c.name
            order by sum(t.amount) desc
            """)
    List<CategorySpending> spendingByCategory(@Param("from") LocalDate from,
                                              @Param("to") LocalDate to);

    @Query("""
            select new com.example.finance.tracker.transaction.repository.MonthlySums(
                 extract(month from t.date),
                 coalesce(sum(case when t.type = com.example.finance.tracker.transaction.TransactionType.INCOME
                                   then t.amount end), 0),
                 coalesce(sum(case when t.type = com.example.finance.tracker.transaction.TransactionType.EXPENSE
                                   then t.amount end), 0))
            from Transaction t
            where t.transferId is null
                 and t.date >= :from and t.date <= :to
            group by extract(month from t.date)
            order by extract(month from t.date)
            """)
    List<MonthlySums> monthlyTotals(@Param("from") LocalDate from,
                                    @Param("to") LocalDate to);

    @Query("""
            select new com.example.finance.tracker.transaction.repository.TopExpense(
                t.id, t.date, t.amount, a.id, a.name, c.name, t.description)
            from Transaction t
            join t.account a
            left join t.category c
            where t.type = com.example.finance.tracker.transaction.TransactionType.EXPENSE
              and t.transferId is null
              and t.date >= :from and t.date <= :to
            order by t.amount desc, t.date desc, t.id desc
            """)
    List<TopExpense> topExpenses(@Param("from") LocalDate from,
                                 @Param("to") LocalDate to,
                                 Pageable pageable);
}