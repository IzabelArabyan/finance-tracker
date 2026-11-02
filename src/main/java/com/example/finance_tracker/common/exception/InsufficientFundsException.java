package com.example.finance_tracker.common.exception;


public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId) {
        super("Not enough money in account" + accountId);
    }
}