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

@AllArgsConstructor
@Slf4j
@Service
public class StudentPollServiceImpl implements StudentPollService {

    private final StudentPollRepository studentPollRepository;
    private final PollRepository pollRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void markPollAsVoted(Long userId, Long pollId) {
        log.debug("Отметка участия пользователя {} в опросе {}", userId, pollId);

        StudentEntity student = getStudentById(userId);
        PollEntity poll = getPollById(pollId);

        StudentPollEntity studentPoll = new StudentPollEntity();
        studentPoll.setStudent(student);
        studentPoll.setPoll(poll);

        try {
            studentPollRepository.save(studentPoll);
            log.debug("Пользователь {} успешно проголосовал в опросе {}", userId, pollId);
        } catch (DataAccessException e) {
            log.error("Ошибка при сохранении голоса пользователя {} в опросе {}: {}", userId, pollId, e.getMessage());
            throw new VotingFailedException("Не удалось сохранить голос из-за ошибки базы данных.", e);
        }
    }

    private StudentEntity getStudentById(Long userId) {
        return studentRepository.findById(userId)
                .orElseThrow(() -> new StudentNotFoundForVoteException(userId));
    }

    private PollEntity getPollById(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundForVoteException(pollId));
    }
}