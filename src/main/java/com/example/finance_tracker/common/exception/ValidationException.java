package com.example.finance_tracker.common.exception;


public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}