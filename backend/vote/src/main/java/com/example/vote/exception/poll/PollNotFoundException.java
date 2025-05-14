package com.example.vote.exception.poll;

public class PollNotFoundException extends RuntimeException {

    public PollNotFoundException(Long id) {
        super("Poll with ID " + id + " not found.");
    }

    public PollNotFoundException(String message) {
        super(message);
    }

    public PollNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
