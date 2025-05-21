package com.example.vote.service;

import com.example.vote.dto.OptionDto;

import java.util.List;

public interface OptionService {

    OptionDto createOption(Long pollId, OptionDto optionDto);

    List<OptionDto> getOptionsByPollId(Long pollId);

    OptionDto getOptionById(Long optionId);
}
