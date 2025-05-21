package com.example.vote.service;


public interface StudentPollService {

    void markPollAsVoted(Long userId, Long pollId);

}
