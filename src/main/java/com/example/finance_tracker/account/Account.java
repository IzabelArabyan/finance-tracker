package com.example.finance_tracker.account;


import java.math.BigDecimal;
import java.time.Instant;

public final class Account {
    private Long id;
    private String name;
    private BigDecimal balance;
    private Instant createdAt;

    public Account() {
    }

    public Account(Long id, String name, BigDecimal balance, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}