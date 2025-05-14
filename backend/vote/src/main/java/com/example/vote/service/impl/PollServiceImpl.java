package com.example.vote.service.impl;

import com.example.vote.dto.OptionDto;
import com.example.vote.dto.PollDto;
import com.example.vote.entity.PollEntity;
import com.example.vote.repository.PollRepository;
import com.example.vote.service.PollService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;

    public PollServiceImpl(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public PollDto createPoll(PollDto pollDTO) {
        PollEntity pollEntity = new PollEntity();
        pollEntity.setQuestion(pollDTO.getQuestion());

        pollEntity = pollRepository.save(pollEntity);

        return new PollDto(pollEntity.getId(), pollEntity.getQuestion(), List.of());
    }

    @Override
    public PollDto getPollById(Long id) {
        PollEntity pollEntity = pollRepository.findById(id).orElseThrow(() -> new RuntimeException("Опрос не найден"));
        List<OptionDto> options = pollEntity.getOptions().stream()
                .map(option -> new OptionDto(option.getId(), option.getOptionText()))
                .collect(Collectors.toList());

        return new PollDto(pollEntity.getId(), pollEntity.getQuestion(), options);
    }

    @Override
    public List<PollDto> getAllPolls() {
        return pollRepository.findAll().stream()
                .map(poll -> new PollDto(poll.getId(), poll.getQuestion(),
                        poll.getOptions().stream()
                                .map(option -> new OptionDto(option.getId(), option.getOptionText()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePoll(Long id) {
        pollRepository.deleteById(id);
    }

    public List<PollDto> getAvailablePollsForUser(Long userId) {
        return pollRepository.findAvailablePollsForUser(userId).stream()
                .map(poll -> new PollDto(poll.getId(), poll.getQuestion(),
                        poll.getOptions().stream()
                                .map(option -> new OptionDto(option.getId(), option.getOptionText()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

}
