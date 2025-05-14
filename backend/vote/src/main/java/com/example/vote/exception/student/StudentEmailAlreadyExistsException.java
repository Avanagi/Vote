package com.example.vote.exception.student;

public class StudentEmailAlreadyExistsException extends RuntimeException {

    public StudentEmailAlreadyExistsException(String email) {
        super("Student with email " + email + " already exists.");
    }

    public StudentEmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
