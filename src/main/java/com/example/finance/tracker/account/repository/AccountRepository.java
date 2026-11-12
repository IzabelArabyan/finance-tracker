package com.example.finance.tracker.account.repository;

import com.example.finance.tracker.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    //all my accounts
    List<Account> findAllByOwnerIdOrderByIdAsc(Long ownerId);
    //one account
    Optional<Account> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("select coalesce(sum(a.balance), 0) from Account a where a.owner.id = :ownerId")
    BigDecimal totalBalance(@Param("ownerId") Long ownerId);
}