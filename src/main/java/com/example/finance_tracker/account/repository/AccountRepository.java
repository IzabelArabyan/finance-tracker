package com.example.finance_tracker.account.repository;

import com.example.finance_tracker.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select coalesce(sum(a.balance), 0) from Account a")
    BigDecimal totalBalance();
}