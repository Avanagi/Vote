package com.example.vote.service.impl;

import com.example.vote.dto.OptionDto;
import com.example.vote.entity.OptionEntity;
import com.example.vote.entity.PollEntity;
import com.example.vote.repository.OptionRepository;
import com.example.vote.repository.PollRepository;
import com.example.vote.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final PollRepository pollRepository;

    public OptionServiceImpl(OptionRepository optionRepository, PollRepository pollRepository) {
        this.optionRepository = optionRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public OptionDto createOption(Long pollId, OptionDto optionDTO) {
        PollEntity poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Опрос не найден"));

        OptionEntity optionEntity = new OptionEntity();
        optionEntity.setPoll(poll);
        optionEntity.setOptionText(optionDTO.getOptionText());

        optionEntity = optionRepository.save(optionEntity);

        return new OptionDto(optionEntity.getId(), optionEntity.getOptionText());
    }

    @Override
    public List<OptionDto> getOptionsByPollId(Long pollId) {
        return optionRepository.findByPollId(pollId).stream()
                .map(option -> new OptionDto(option.getId(), option.getOptionText()))
                .collect(Collectors.toList());
    }
}
