package com.example.vote.service;

import com.example.vote.dto.PollDto;

import java.util.List;

public interface PollService {

    void createPoll(PollDto pollDto);

    PollDto getPollById(Long id);

    List<PollDto> getAllPolls();

    PollDto updatePoll(Long pollId, PollDto pollDTO);

    void deletePoll(Long id);

    List<PollDto> getAvailablePollsForUserAndGroup(Long userId,  String group);

}
