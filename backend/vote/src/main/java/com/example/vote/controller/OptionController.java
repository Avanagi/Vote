package com.example.vote.controller;

import com.example.vote.dto.OptionDto;
import com.example.vote.exception.option.OptionCreationException;
import com.example.vote.exception.option.PollNotFoundForOptionException;
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

    @PostMapping("/{pollId}")
    public ResponseEntity<String> createOption(@PathVariable Long pollId, @RequestBody OptionDto optionDto) {
        try {
            optionService.createOption(pollId, optionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Опция успешно создана.");
        } catch (PollNotFoundForOptionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Опрос не найден.");
        } catch (OptionCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при создании опции: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неожиданная ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/poll/{pollId}")
    public ResponseEntity<?> getOptionsByPollId(@PathVariable Long pollId) {
        try {
            List<OptionDto> options = optionService.getOptionsByPollId(pollId);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении опций: " + e.getMessage());
        }
    }

    @GetMapping("/{optionId}")
    public ResponseEntity<?> getOptionById(@PathVariable Long optionId) {
        try {
            OptionDto option = optionService.getOptionById(optionId);
            return ResponseEntity.ok(option);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении опции: " + e.getMessage());
        }
    }
}
