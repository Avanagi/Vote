package com.example.vote.service;

import com.example.vote.entity.PollEntity;

import java.util.List;

public interface StudentPollService {

    void markPollAsVoted(Long userId, Long pollId);

    List<PollEntity> getAvailablePolls(Long userId);

}
