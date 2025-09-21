package com.saygobackend.exception;

public class InvalidBudgetRangeException extends RuntimeException {
    public InvalidBudgetRangeException(String message) {
        super(message);
    }
}
