package com.example.finance_tracker.account;

import java.math.BigDecimal;
import java.time.Instant;

public class Account {
    private final Long id;
    private final String name;
    private final BigDecimal balance;
    private final Instant createdAt;

    public Account(Long id, String name, BigDecimal balance, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Account withName(String newName) {
        return new Account(id, newName, balance, createdAt);
    }

    public Account withBalance(BigDecimal newBalance) {
        return new Account(id, name, newBalance, createdAt);
    }
}
