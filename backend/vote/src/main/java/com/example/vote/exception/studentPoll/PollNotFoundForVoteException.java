package com.example.vote.exception.studentPoll;

public class PollNotFoundForVoteException extends RuntimeException {

    public PollNotFoundForVoteException(Long pollId) {
        super("Poll with ID " + pollId + " not found.");
    }

    public PollNotFoundForVoteException(String message) {
        super(message);
    }

    public PollNotFoundForVoteException(String message, Throwable cause) {
        super(message, cause);
    }

}
