package com.example.vote.controller;

import com.example.vote.dto.OptionDto;
import com.example.vote.exception.option.OptionCreationException;
import com.example.vote.exception.option.PollNotFoundForOptionException;
import com.example.vote.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/{pollId}")
    public ResponseEntity<OptionDto> createOption(@PathVariable Long pollId, @RequestBody OptionDto optionDTO) {
        log.info("Creating option for poll ID {}: {}", pollId, optionDTO);
        try {
            OptionDto createdOption = optionService.createOption(pollId, optionDTO);
            return new ResponseEntity<>(createdOption, HttpStatus.CREATED);
        } catch (PollNotFoundForOptionException e) {
            log.error("Poll not found for creating option (poll ID {}): {}", pollId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (OptionCreationException e) {
            log.error("Error creating option for poll ID {}: {}", pollId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error creating option for poll ID {}: {}", pollId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/poll/{pollId}")
    public ResponseEntity<List<OptionDto>> getOptionsByPollId(@PathVariable Long pollId) {
        log.info("Getting options for poll ID: {}", pollId);
        try {
            List<OptionDto> options = optionService.getOptionsByPollId(pollId);
            return new ResponseEntity<>(options, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unexpected error getting options for poll ID {}: {}", pollId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{optionId}")
    public ResponseEntity<OptionDto> getOptionById(@PathVariable Long optionId) {
        log.info("Getting option by ID: {}", optionId);
        try {
            OptionDto option = optionService.getOptionById(optionId);
            return new ResponseEntity<>(option, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unexpected error getting options for poll ID {}: {}", optionId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred in OptionController", ex);
        return new ResponseEntity<>("Произошла непредвиденная ошибка.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}