package com.example.finance_tracker.account.dto;

import java.math.BigDecimal;

public record CreateAccountRequest(String name, BigDecimal initialBalance) {
}