package com.example.finance_tracker.transaction.dto;

import com.example.finance_tracker.transaction.Transaction;
import com.example.finance_tracker.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(Long id,
                                  Long accountId,
                                  Long categoryId,
                                  BigDecimal amount,
                                  TransactionType type,
                                  LocalDate date,
                                  String description) {
    public static TransactionResponse from(Transaction transaction){
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getDescription());
    }
}
