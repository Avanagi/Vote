package com.example.vote.service.impl;

import com.example.vote.dto.OptionDto;
import com.example.vote.dto.PollDto;
import com.example.vote.entity.OptionEntity;
import com.example.vote.entity.PollEntity;
import com.example.vote.exception.poll.PollCreationException;
import com.example.vote.exception.poll.PollDeletionException;
import com.example.vote.exception.poll.PollNotFoundException;
import com.example.vote.mapper.OptionMapper;
import com.example.vote.mapper.PollMapper;
import com.example.vote.repository.OptionRepository;
import com.example.vote.repository.PollRepository;
import com.example.vote.service.PollService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final OptionRepository optionRepository;
    private final PollMapper pollMapper;

    @Override
    @Transactional
    public PollDto createPoll(PollDto pollDTO) {
        log.info("Creating poll: {}", pollDTO.getQuestion());
        PollEntity pollEntity = new PollEntity();
        pollEntity.setQuestion(pollDTO.getQuestion());
        pollEntity.setOptions(new ArrayList<>());
        try {
            log.info(pollEntity.toString());
            PollEntity savedPollEntity = pollRepository.save(pollEntity);
            log.info("Poll created successfully with ID: {}", savedPollEntity.getId());

            List<OptionEntity> optionEntities = new ArrayList<>();
            if (pollDTO.getOptions() != null && !pollDTO.getOptions().isEmpty()) {
                for (OptionDto optionDTO : pollDTO.getOptions()) {
                    log.info("Adding option: {}, {}", savedPollEntity.getId(), optionDTO.getOptionText());
                    OptionEntity optionEntity = new OptionEntity();
                    optionEntity.setOptionText(optionDTO.getOptionText());
                    optionEntity.setPoll(savedPollEntity);
                    optionEntities.add(optionEntity);
                }
                optionRepository.saveAll(optionEntities);
            }

            return pollMapper.toDto(savedPollEntity);
        } catch (DataAccessException e) {
            log.error("Error during poll creation: {}", pollDTO.getQuestion(), e);
            throw new PollCreationException("Failed to create poll due to database error.", e);
        }
    }



    @Override
    @Transactional(readOnly = true)
    public PollDto getPollById(Long id) {
        log.info("Getting poll by ID: {}", id);
        PollEntity pollEntity = pollRepository.findById(id)
                .orElseThrow(() -> new PollNotFoundException(id));
        return pollMapper.toDtoWithOption(pollEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PollDto> getAllPolls() {
        log.info("Getting all polls");
        return pollRepository.findAll().stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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

    @Transactional
    public List<PollDto> getAvailablePollsForUser(Long userId) {
        log.info("Getting available polls for user ID: {}", userId);
        return pollRepository.findAvailablePollsForUser(userId).stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }
}