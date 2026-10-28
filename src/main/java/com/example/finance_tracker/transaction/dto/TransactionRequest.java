package com.example.finance_tracker.transaction.dto;

import com.example.finance_tracker.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(Long accountId,
                                 Long categoryId,
                                 BigDecimal amount,
                                 TransactionType type,
                                 LocalDate date,
                                 String description) {
}
