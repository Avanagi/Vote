package com.example.vote.exception.option;

public class OptionCreationException extends RuntimeException {

    public OptionCreationException(String message) {
        super(message);
    }

    public OptionCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
