package com.example.vote.controller;

import com.example.vote.dto.PollDto;
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

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping
    public ResponseEntity<PollDto> createPoll(@RequestBody PollDto pollDTO) {
        PollDto createdPoll = pollService.createPoll(pollDTO);
        return new ResponseEntity<>(createdPoll, HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/{id}")
    public ResponseEntity<PollDto> getPollById(@PathVariable Long id) {
        PollDto pollDTO = pollService.getPollById(id);
        return new ResponseEntity<>(pollDTO, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping
    public List<PollDto> getAllPolls() {
        return pollService.getAllPolls();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/{pollId},{userId}/vote")
    public ResponseEntity<String> vote(@PathVariable Long pollId, @PathVariable Long userId) {
        userPollService.markPollAsVoted(userId, pollId);
        return new ResponseEntity<>("Ваш голос учтён. Опрос скрыт для вас.", HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/available")
    public ResponseEntity<List<PollDto>> getAvailablePolls(@RequestParam Long userId) {
        List<PollDto> availablePolls = pollService.getAvailablePollsForUser(userId);
        return new ResponseEntity<>(availablePolls, HttpStatus.OK);
    }
}
