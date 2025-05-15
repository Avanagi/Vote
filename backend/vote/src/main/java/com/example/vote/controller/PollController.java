package com.example.vote.controller;

import com.example.vote.dto.PollDto;
import com.example.vote.exception.poll.PollCreationException;
import com.example.vote.exception.poll.PollDeletionException;
import com.example.vote.exception.poll.PollNotFoundException;
import com.example.vote.service.PollService;
import com.example.vote.service.StudentPollService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;
    private final StudentPollService userPollService;

    public PollController(PollService pollService, StudentPollService userPollService) {
        this.pollService = pollService;
        this.userPollService = userPollService;
    }

    @PostMapping
    public ResponseEntity<PollDto> createPoll(@RequestBody PollDto pollDTO) {
        log.info("Creating poll: {}", pollDTO);
        try {
            PollDto createdPoll = pollService.createPoll(pollDTO);
            return new ResponseEntity<>(createdPoll, HttpStatus.CREATED);
        } catch (PollCreationException e) {
            log.error("Error creating poll: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PollDto> getPollById(@PathVariable Long id) {
        log.info("Get poll by id: {}", id);
        try {
            PollDto pollDTO = pollService.getPollById(id);
            return new ResponseEntity<>(pollDTO, HttpStatus.OK);
        } catch (PollNotFoundException e) {
            log.error("Poll not found with id {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<PollDto>> getAllPolls() {
        log.info("Get all polls");
        List<PollDto> polls = pollService.getAllPolls();
        return new ResponseEntity<>(polls, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        log.info("Delete poll by id: {}", id);
        try {
            pollService.deletePoll(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (PollNotFoundException e) {
            log.error("Poll not found for deletion with id {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PollDeletionException e) {
            log.error("Error deleting poll with id {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{pollId},{userId}/vote")
    public ResponseEntity<String> vote(@PathVariable Long pollId, @PathVariable Long userId) {
        log.info("Vote for poll {} by user {}", pollId, userId);
        try {
            userPollService.markPollAsVoted(userId, pollId);
            return new ResponseEntity<>("Ваш голос учтён. Опрос скрыт для вас.", HttpStatus.OK);
        } catch (Exception e) { // Обработайте более специфичные исключения, если они есть в StudentPollService
            log.error("Error during vote for poll {} by user {}: {}", pollId, userId, e.getMessage());
            return new ResponseEntity<>("Произошла ошибка при голосовании.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<PollDto>> getAvailablePolls(@RequestParam Long userId) {
        log.info("Get available polls for user id: {}", userId);
        List<PollDto> availablePolls = pollService.getAvailablePollsForUser(userId);
        return new ResponseEntity<>(availablePolls, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred in PollController", ex);
        return new ResponseEntity<>("Произошла непредвиденная ошибка.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}