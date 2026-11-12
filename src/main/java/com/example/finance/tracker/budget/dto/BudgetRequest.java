package com.example.finance.tracker.budget.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetRequest(

        @NotNull(message = "categoryId is required")
        Long categoryId,

        @NotNull(message = "month is required, e.g. 2026-10")
        YearMonth month,

        @NotNull(message = "limit is required")
        @Positive(message = "limit must be greater than 0")
        @Digits(integer = 15, fraction = 2, message = "limit can have at most 2 decimals")
        BigDecimal limit) {
}