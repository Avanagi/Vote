package com.example.vote.exception.human;

public class HumanSaveFailedException extends RuntimeException {

    public HumanSaveFailedException(String message) {
        super(message);
    }

    public HumanSaveFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
