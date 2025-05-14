package com.example.vote.exception.student;

public class StudentDeleteFailedException extends RuntimeException {

    public StudentDeleteFailedException(Long id) {
        super("Failed to delete student with ID " + id + ".");
    }

    public StudentDeleteFailedException(String message) {
        super(message);
    }

    public StudentDeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
