package com.example.vote.service.impl;

import com.example.vote.entity.PollEntity;
import com.example.vote.entity.StudentEntity;
import com.example.vote.entity.StudentPollEntity;
import com.example.vote.repository.PollRepository;
import com.example.vote.repository.StudentRepository;
import com.example.vote.repository.StudentPollRepository;
import com.example.vote.service.StudentPollService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentPollServiceImpl implements StudentPollService {
    private final StudentPollRepository userPollRepository;

    private final PollRepository pollRepository;

    private final StudentRepository studentRepository;

    public StudentPollServiceImpl(StudentPollRepository userPollRepository, PollRepository pollRepository, StudentRepository studentRepository) {
        this.userPollRepository = userPollRepository;
        this.pollRepository = pollRepository;
        this.studentRepository = studentRepository;
    }

    public void markPollAsVoted(Long userId, Long pollId) {
        StudentEntity student = studentRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Студент с ID " + userId + " не найден"));

        PollEntity poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("Опрос с ID " + pollId + " не найден"));

        StudentPollEntity userPoll = new StudentPollEntity();
        userPoll.setStudent(student);
        userPoll.setPoll(poll);

        userPollRepository.save(userPoll);
    }

    public List<PollEntity> getAvailablePolls(Long userId) {
        return pollRepository.findAvailablePollsForUser(userId);
    }
}

