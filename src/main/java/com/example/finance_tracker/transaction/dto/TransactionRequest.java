package com.example.finance_tracker.transaction.dto;

import com.example.finance_tracker.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
public record TransactionRequest(Long accountId,
                                 Long categoryId,
                                 @NotNull(message = "amount is required")
                                 @Positive(message = "amount must be greater than 0")
                                 @Digits(integer = 15, fraction = 2, message = "amount can have at most 2 decimals")
                                 BigDecimal amount,
                                 TransactionType type,
                                 LocalDate date,
                                 String description) {

}
