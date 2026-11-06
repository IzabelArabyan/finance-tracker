package com.example.finance.tracker.common.exception;


public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}