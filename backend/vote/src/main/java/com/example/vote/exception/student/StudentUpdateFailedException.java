package com.example.vote.exception.student;

public class StudentUpdateFailedException extends RuntimeException {

    public StudentUpdateFailedException(Long id) {
        super("Failed to update student with ID " + id + ".");
    }

    public StudentUpdateFailedException(String message) {
        super(message);
    }

    public StudentUpdateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
