package com.example.vote.service.impl;

import com.example.vote.entity.PollEntity;
import com.example.vote.entity.StudentEntity;
import com.example.vote.entity.StudentPollEntity;
import com.example.vote.exception.studentPoll.PollNotFoundForVoteException;
import com.example.vote.exception.studentPoll.StudentNotFoundForVoteException;
import com.example.vote.exception.studentPoll.VotingFailedException;
import com.example.vote.repository.PollRepository;
import com.example.vote.repository.StudentPollRepository;
import com.example.vote.repository.StudentRepository;
import com.example.vote.service.StudentPollService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class StudentPollServiceImpl implements StudentPollService {

    private final StudentPollRepository studentPollRepository;
    private final PollRepository pollRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void markPollAsVoted(Long userId, Long pollId) {
        log.info("Marking poll {} as voted by user {}", pollId, userId);
        StudentEntity student = studentRepository.findById(userId)
                .orElseThrow(() -> new StudentNotFoundForVoteException(userId));

        PollEntity poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundForVoteException(pollId));

        StudentPollEntity userPoll = new StudentPollEntity();
        userPoll.setStudent(student);
        userPoll.setPoll(poll);

        try {
            studentPollRepository.save(userPoll);
            log.info("Poll {} marked as voted by user {} successfully.", pollId, userId);
        } catch (DataAccessException e) {
            log.error("Error marking poll {} as voted by user {}: {}", pollId, userId, e.getMessage());
            throw new VotingFailedException("Failed to record vote due to database error.", e);
        }
    }
}