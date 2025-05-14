package com.example.vote.exception.studentPoll;

public class StudentNotFoundForVoteException extends RuntimeException {

    public StudentNotFoundForVoteException(Long userId) {
        super("Student with ID " + userId + " not found.");
    }

    public StudentNotFoundForVoteException(String message) {
        super(message);
    }

    public StudentNotFoundForVoteException(String message, Throwable cause) {
        super(message, cause);
    }

}
