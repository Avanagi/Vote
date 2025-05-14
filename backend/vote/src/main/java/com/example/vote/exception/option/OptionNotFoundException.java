package com.example.vote.exception.option;

public class OptionNotFoundException extends RuntimeException {

    public OptionNotFoundException(Long id) {
        super("Option with ID " + id + " not found.");
    }

    public OptionNotFoundException(String message) {
        super(message);
    }

    public OptionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
