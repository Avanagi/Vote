package com.example.vote.exception.human;

public class HumanUpdateFailedException extends RuntimeException {

    public HumanUpdateFailedException(Long id) {
        super("Failed to update human with ID " + id + ".");
    }

    public HumanUpdateFailedException(String message) {
        super(message);
    }

    public HumanUpdateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
