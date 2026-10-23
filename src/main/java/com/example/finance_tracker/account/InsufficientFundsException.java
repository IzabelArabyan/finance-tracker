package com.example.finance_tracker.account;


import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long id, BigDecimal balance, BigDecimal amount) {
        super("Account " + id + " has balance " + balance + ", cannot withdraw " + amount);
    }
}