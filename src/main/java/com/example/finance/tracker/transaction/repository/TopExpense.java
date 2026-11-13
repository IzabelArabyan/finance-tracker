package com.example.finance.tracker.transaction.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
public record TopExpense(Long transactionId,
                         LocalDate date,
                         BigDecimal amount,
                         Long accountId,
                         String accountName,
                         String categoryName,
                         String decription) {
}
