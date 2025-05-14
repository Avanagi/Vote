package com.example.vote.exception.student;

public class StudentSaveFailedException extends RuntimeException {

    public StudentSaveFailedException(String message) {
        super(message);
    }

    public StudentSaveFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
