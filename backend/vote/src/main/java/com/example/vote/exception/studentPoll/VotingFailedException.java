package com.example.vote.exception.studentPoll;

public class VotingFailedException extends RuntimeException {

    public VotingFailedException(String message) {
        super(message);
    }

    public VotingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
