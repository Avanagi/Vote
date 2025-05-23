package com.example.vote.service;

import com.example.vote.dto.OptionDto;

import java.util.List;

public interface OptionService {

    void createOption(Long pollId, OptionDto optionDto);

    List<OptionDto> getOptionsByPollId(Long pollId);

    OptionDto getOptionById(Long optionId);
}
