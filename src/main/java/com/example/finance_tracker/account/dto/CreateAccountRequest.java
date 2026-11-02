package com.example.finance_tracker.account.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "name must not be blank")
        String name,

        @PositiveOrZero(message = "initialBalance must be 0 or more")
        @Digits(integer = 15, fraction = 2, message = "initialBalance can have at most 2 decimals")
        BigDecimal initialBalance
) {
}