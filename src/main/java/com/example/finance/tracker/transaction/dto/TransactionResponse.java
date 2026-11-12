package com.example.finance.tracker.transaction.dto;

import com.example.finance.tracker.transaction.Transaction;
import com.example.finance.tracker.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(Long id,
                                  Long accountId,
                                  String accountName,
                                  Long categoryId,
                                  String categoryName,
                                  BigDecimal amount,
                                  TransactionType type,
                                  LocalDate date,
                                  String description,
                                  UUID transferId) {
    public static TransactionResponse from(Transaction transaction){

        // An INCOME may have no category
        Long categoryId = null;
        String categoryName = null;
        if (transaction.getCategory() != null) {
            categoryId = transaction.getCategory().getId();
            categoryName = transaction.getCategory().getName();
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getAccount().getName(),
                categoryId,
                categoryName,
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getTransferId());
    }
}
