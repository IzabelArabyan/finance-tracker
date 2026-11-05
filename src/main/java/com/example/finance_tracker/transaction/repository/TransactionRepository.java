package com.example.finance_tracker.transaction.repository;

import com.example.finance_tracker.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    List<Transaction> findByAccountId(Long accountId);

    boolean existsByAccountId(Long accountId);

    boolean existsByCategoryId(Long categoryId);
}