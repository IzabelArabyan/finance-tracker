package com.example.finance_tracker.account;

import java.math.BigDecimal;

public class CreateAccountRequest {

    private String name;
    private BigDecimal balance;

    public CreateAccountRequest() {
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
}