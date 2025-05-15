package com.example.vote.exception.option;

public class PollNotFoundForOptionException extends RuntimeException {

    public PollNotFoundForOptionException(Long pollId) {
        super("Poll with ID " + pollId + " not found while creating option.");
    }

    public PollNotFoundForOptionException(String message) {
        super(message);
    }

    public PollNotFoundForOptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
