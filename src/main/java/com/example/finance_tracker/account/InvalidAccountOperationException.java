package com.example.finance_tracker.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAccountOperationException extends RuntimeException {

    public InvalidAccountOperationException(String message) {
        super(message);
    }
}