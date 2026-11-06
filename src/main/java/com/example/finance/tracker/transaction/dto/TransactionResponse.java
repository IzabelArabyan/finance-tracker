package com.example.finance.tracker.transaction.dto;

import com.example.finance.tracker.transaction.Transaction;
import com.example.finance.tracker.transaction.TransactionType;

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

        // An INCOME may have no category
        Long categoryId = null;
        if (transaction.getCategory() != null) {
            categoryId = transaction.getCategory().getId();
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getCategory().getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getDescription());
    }
}
