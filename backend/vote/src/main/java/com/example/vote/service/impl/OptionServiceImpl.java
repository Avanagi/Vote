package com.example.vote.service.impl;

import com.example.vote.dto.OptionDto;
import com.example.vote.entity.OptionEntity;
import com.example.vote.entity.PollEntity;
import com.example.vote.exception.option.OptionCreationException;
import com.example.vote.exception.option.PollNotFoundForOptionException;
import com.example.vote.mapper.OptionMapper;
import com.example.vote.repository.OptionRepository;
import com.example.vote.repository.PollRepository;
import com.example.vote.service.OptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final PollRepository pollRepository;
    private final OptionMapper optionMapper;

    @Override
    @Transactional
    public OptionDto createOption(Long pollId, OptionDto optionDTO) {
        log.debug("Creating option for poll with ID {}: {}", pollId, optionDTO.getOptionText());
        PollEntity poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundForOptionException(pollId));

        OptionEntity optionEntity = optionMapper.toEntity(optionDTO);
        optionEntity.setPoll(poll);

        try {
            OptionEntity savedOptionEntity = optionRepository.save(optionEntity);
            log.debug("Option created successfully with ID: {} for poll ID: {}", savedOptionEntity.getId(), pollId);
            return optionMapper.toDto(savedOptionEntity);
        } catch (DataAccessException e) {
            log.error("Error creating option for poll with ID {}: {}", pollId, e.getMessage());
            throw new OptionCreationException("Failed to create option due to database error.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionDto> getOptionsByPollId(Long pollId) {
        log.debug("Getting options for poll with ID: {}", pollId);
        return optionRepository.findByPollId(pollId).stream()
                .map(optionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OptionDto getOptionById(Long optionId) {
        log.debug("Getting option by ID: {}", optionId);
        OptionEntity optionEntity = optionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("Option not found with ID: " + optionId));
        return optionMapper.toDto(optionEntity);
    }

}