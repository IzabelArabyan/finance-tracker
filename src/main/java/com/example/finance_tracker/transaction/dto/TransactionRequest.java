package com.example.finance_tracker.transaction.dto;

import com.example.finance_tracker.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record TransactionRequest(@NotNull(message = "accountId is required")
                                 Long accountId,

                                 Long categoryId,

                                 @NotNull(message = "amount is required")
                                 @Positive(message = "amount must be greater than 0")
                                 @Digits(integer = 15, fraction = 2, message = "amount can have at most 2 decimals")
                                 BigDecimal amount,

                                 @NotNull(message = "type must be INCOME or EXPENSE")
                                 TransactionType type,

                                 //if missing, today is used
                                 @PastOrPresent(message = "date must not be in the future")
                                 LocalDate date,

                                 String description) {

}
