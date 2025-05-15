package com.example.vote.exception.human;

public class HumanEmailAlreadyExistsException extends RuntimeException {

    public HumanEmailAlreadyExistsException(String email) {
        super("Human with email " + email + " already exists.");
    }

    public HumanEmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
