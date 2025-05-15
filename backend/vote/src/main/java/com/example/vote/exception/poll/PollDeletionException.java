package com.example.vote.exception.poll;

public class PollDeletionException extends RuntimeException {

    public PollDeletionException(Long id) {
        super("Failed to delete poll with ID " + id + ".");
    }

    public PollDeletionException(String message) {
        super(message);
    }

    public PollDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

}
