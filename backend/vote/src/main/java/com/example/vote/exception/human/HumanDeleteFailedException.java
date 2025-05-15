package com.example.vote.exception.human;

public class HumanDeleteFailedException extends RuntimeException {

    public HumanDeleteFailedException(Long id) {
        super("Failed to delete human with ID " + id + ".");
    }

    public HumanDeleteFailedException(String message) {
        super(message);
    }

    public HumanDeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
