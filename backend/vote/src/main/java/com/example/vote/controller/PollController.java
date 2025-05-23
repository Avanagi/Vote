package com.example.vote.controller;

import com.example.vote.dto.PollDto;
import com.example.vote.exception.poll.PollCreationException;
import com.example.vote.exception.poll.PollDeletionException;
import com.example.vote.exception.poll.PollNotFoundException;
import com.example.vote.service.PollService;
import com.example.vote.service.StudentPollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> createPoll(@RequestBody PollDto pollDTO) {
        try {
            pollService.createPoll(pollDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Успешно создано");
        } catch (PollCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при создании опроса: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPollById(@PathVariable Long id) {
        try {
            PollDto poll = pollService.getPollById(id);
            return ResponseEntity.ok(poll);
        } catch (PollNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Опрос не найден.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении опроса: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PollDto>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePoll(@PathVariable Long id) {
        try {
            pollService.deletePoll(id);
            return ResponseEntity.ok("Опрос успешно удалён.");
        } catch (PollNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Опрос не найден.");
        } catch (PollDeletionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении опроса: " + e.getMessage());
        }
    }

    @PostMapping("/{pollId},{userId}/vote")
    public ResponseEntity<String> vote(@PathVariable Long pollId, @PathVariable Long userId) {
        try {
            userPollService.markPollAsVoted(userId, pollId);
            return ResponseEntity.ok("Ваш голос учтён. Опрос скрыт для вас.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при голосовании: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePoll(@PathVariable Long id, @RequestBody PollDto pollDto) {
        try {
            PollDto updatedPoll = pollService.updatePoll(id, pollDto);
            return ResponseEntity.ok(updatedPoll);
        } catch (PollNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Опрос не найден.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении опроса: " + e.getMessage());
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailablePolls(@RequestParam Long userId,
                                               @RequestParam(required = false) String group) {
        try {
            List<PollDto> availablePolls = pollService.getAvailablePollsForUserAndGroup(userId, group);
            return ResponseEntity.ok(availablePolls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении доступных опросов: " + e.getMessage());
        }
    }
}
