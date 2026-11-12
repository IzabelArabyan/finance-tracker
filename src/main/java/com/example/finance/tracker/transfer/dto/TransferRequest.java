package com.example.finance.tracker.transfer.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "fromAccountId is required")
        Long fromAccountId,

        @NotNull(message = "toAccountId is required")
        Long toAccountId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than 0")
        @Digits(integer = 15, fraction = 2, message = "amount can have at most 2 decimals")
        BigDecimal amount) {
}