package com.example.vote.controller;

import com.example.vote.dto.OptionDto;
import com.example.vote.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/{pollId}")
    public ResponseEntity<OptionDto> createOption(@PathVariable Long pollId, @RequestBody OptionDto optionDTO) {
        OptionDto createdOption = optionService.createOption(pollId, optionDTO);
        return new ResponseEntity<>(createdOption, HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/poll/{pollId}")
    public ResponseEntity<List<OptionDto>> getOptionsByPollId(@PathVariable Long pollId) {
        List<OptionDto> options = optionService.getOptionsByPollId(pollId);
        return new ResponseEntity<>(options, HttpStatus.OK);
    }
}
