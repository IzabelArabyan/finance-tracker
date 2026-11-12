package com.example.finance.tracker.budget.repository;

import com.example.finance.tracker.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    //the budget of one category in one month
    Optional<Budget> findByOwnerIdAndCategoryIdAndMonth(Long ownerId, Long categoryId, LocalDate month);


    boolean existsByCategoryId(Long categoryId);

    // All budgets of required month
    @Query("""
            select b from Budget b
            join fetch b.category c
            where b.month = :month and b.owner.id = :ownerId
            order by c.name
            """)
    List<Budget> findAllForMonth(@Param("month") LocalDate month, @Param("ownerId") Long ownerId);;
}