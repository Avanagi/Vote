package com.example.vote.service.impl;

import com.example.vote.dto.PollDto;
import com.example.vote.entity.PollEntity;
import com.example.vote.exception.poll.PollCreationException;
import com.example.vote.exception.poll.PollDeletionException;
import com.example.vote.exception.poll.PollNotFoundException;
import com.example.vote.mapper.OptionMapper;
import com.example.vote.mapper.PollMapper;
import com.example.vote.repository.PollRepository;
import com.example.vote.service.PollService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final PollMapper pollMapper;

    public PollServiceImpl(PollRepository pollRepository, PollMapper pollMapper) {
        this.pollRepository = pollRepository;
        this.pollMapper = pollMapper;
    }

    @Override
    public PollDto createPoll(PollDto pollDTO) {
        log.info("Creating poll: {}", pollDTO.getQuestion());
        PollEntity pollEntity = pollMapper.toEntity(pollDTO);

        try {
            PollEntity savedPollEntity = pollRepository.save(pollEntity);
            log.info("Poll created successfully with ID: {}", savedPollEntity.getId());
            return pollMapper.toDto(savedPollEntity);
        } catch (DataAccessException e) {
            log.error("Error during poll creation: {}", pollDTO.getQuestion(), e);
            throw new PollCreationException("Failed to create poll due to database error.", e);
        }
    }

    @Override
    public PollDto getPollById(Long id) {
        log.info("Getting poll by ID: {}", id);
        PollEntity pollEntity = pollRepository.findById(id)
                .orElseThrow(() -> new PollNotFoundException(id));
        return pollMapper.toDtoWithOption(pollEntity);
    }

    @Override
    public List<PollDto> getAllPolls() {
        log.info("Getting all polls");
        return pollRepository.findAll().stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePoll(Long id) {
        log.info("Deleting poll with ID: {}", id);
        try {
            if (!pollRepository.existsById(id)) {
                throw new PollNotFoundException(id);
            }
            pollRepository.deleteById(id);
            log.info("Poll with ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            log.error("Error during poll deletion with ID {}: {}", id, e.getMessage());
            throw new PollDeletionException(id.toString(), e);
        }
    }

    public List<PollDto> getAvailablePollsForUser(Long userId) {
        log.info("Getting available polls for user ID: {}", userId);
        return pollRepository.findAvailablePollsForUser(userId).stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }
}