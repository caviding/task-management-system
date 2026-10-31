package com.example.exception;

public class TaskUserMismatchException extends RuntimeException {
    public TaskUserMismatchException(String message) {
        super(message);
    }
}
