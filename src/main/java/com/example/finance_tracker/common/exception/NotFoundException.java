package com.example.finance_tracker.common.exception;


public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, Long id) {
        super(entity + " " + id + " not found");
    }
}