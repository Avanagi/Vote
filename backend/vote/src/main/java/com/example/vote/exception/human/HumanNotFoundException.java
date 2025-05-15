package com.example.vote.exception.human;

public class HumanNotFoundException extends RuntimeException {

    public HumanNotFoundException(Long id) {
        super("Human with ID " + id + " not found.");
    }

    public HumanNotFoundException(String message) {
        super(message);
    }

    public HumanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
