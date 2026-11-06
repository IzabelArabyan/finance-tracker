package com.example.finance.tracker.account.dto;

import com.example.finance.tracker.account.Account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(Long id, String name, BigDecimal balance, Instant createdAt) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getBalance(),
                account.getCreatedAt());
    }
}