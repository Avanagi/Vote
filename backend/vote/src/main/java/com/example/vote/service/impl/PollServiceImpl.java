package com.example.vote.service.impl;

import com.example.vote.dto.PollDto;
import com.example.vote.entity.OptionEntity;
import com.example.vote.entity.PollEntity;
import com.example.vote.entity.StudentEntity;
import com.example.vote.exception.poll.PollCreationException;
import com.example.vote.exception.poll.PollDeletionException;
import com.example.vote.exception.poll.PollNotFoundException;
import com.example.vote.mapper.PollMapper;
import com.example.vote.repository.OptionRepository;
import com.example.vote.repository.PollRepository;
import com.example.vote.repository.StudentRepository;
import com.example.vote.service.EmailService;
import com.example.vote.service.PollService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final StudentRepository studentRepository;
    private final OptionRepository optionRepository;
    private final PollMapper pollMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public void createPoll(PollDto pollDTO) {
        log.debug("Создание опроса: {}", pollDTO.getQuestion());

        PollEntity pollEntity = new PollEntity();
        pollEntity.setQuestion(pollDTO.getQuestion());
        pollEntity.setVisibleFor(pollMapper.convertGroupListToString(pollDTO.getVisibleFor()));
        pollEntity.setTeacherId(pollDTO.getTeacherId());
        pollEntity.setCreatedAt(LocalDateTime.now());

        try {
            PollEntity savedPollEntity = pollRepository.save(pollEntity);
            log.debug("Опрос успешно создан с ID: {}", savedPollEntity.getId());

            List<OptionEntity> optionEntities = pollMapper.buildOptionEntities(pollDTO.getOptions(), savedPollEntity);
            optionRepository.saveAll(optionEntities);
            savedPollEntity.setOptions(optionEntities);

            List<StudentEntity> targetStudents = resolveTargetStudents(savedPollEntity.getVisibleFor());
            emailService.sendEmailsToStudents(targetStudents, savedPollEntity);

            pollMapper.toDto(savedPollEntity);

        } catch (DataAccessException e) {
            log.error("Ошибка при создании опроса: {}", pollDTO.getQuestion(), e);
            throw new PollCreationException("Не удалось создать опрос из-за ошибки базы данных.", e);
        }
    }

    @Override
    @Transactional
    public PollDto updatePoll(Long pollId, PollDto pollDTO) {
        log.debug("Обновление опроса с ID: {}", pollId);

        PollEntity existingPoll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundException("Опрос с id " + pollId + " не найден"));

        existingPoll.setQuestion(pollDTO.getQuestion());

        String oldVisibleFor = existingPoll.getVisibleFor();
        String newVisibleFor = pollMapper.convertGroupListToString(pollDTO.getVisibleFor());
        existingPoll.setVisibleFor(newVisibleFor);

        existingPoll.getOptions().clear();
        existingPoll.getOptions().addAll(pollMapper.buildOptionEntities(pollDTO.getOptions(), existingPoll));

        PollEntity updatedPoll = pollRepository.save(existingPoll);

        if (!Objects.equals(
                new HashSet<>(pollMapper.parseGroupString(oldVisibleFor)),
                new HashSet<>(pollMapper.parseGroupString(newVisibleFor))
        )) {
            List<StudentEntity> targetStudents = resolveTargetStudents(newVisibleFor);
            emailService.sendEmailsToStudents(targetStudents, updatedPoll);
        }

        return pollMapper.toDto(updatedPoll);
    }

    @Override
    @Transactional(readOnly = true)
    public PollDto getPollById(Long id) {
        log.debug("Получение опроса по ID: {}", id);
        PollEntity pollEntity = pollRepository.findById(id)
                .orElseThrow(() -> new PollNotFoundException(id));
        return pollMapper.toDtoWithOption(pollEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PollDto> getAllPolls() {
        log.debug("Получение всех опросов");
        return pollRepository.findAll().stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePoll(Long id) {
        log.debug("Удаление опроса с ID: {}", id);
        try {
            if (!pollRepository.existsById(id)) {
                throw new PollNotFoundException(id);
            }
            pollRepository.deleteById(id);
            log.debug("Опрос с ID {} успешно удалён.", id);
        } catch (DataAccessException e) {
            log.error("Ошибка при удалении опроса с ID {}: {}", id, e.getMessage());
            throw new PollDeletionException(id.toString(), e);
        }
    }

    @Override
    @Transactional
    public List<PollDto> getAvailablePollsForUserAndGroup(Long userId, String group) {
        log.debug("Получение доступных опросов для пользователя ID: {}", userId);
        return pollRepository.findAvailablePollsForUserAndGroup(userId, group).stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }

    private List<StudentEntity> resolveTargetStudents(String visibleFor) {
        List<String> groups = pollMapper.parseGroupString(visibleFor);
        return groups.isEmpty()
                ? studentRepository.findAll()
                : studentRepository.findByStudentGroupIn(groups);
    }

}
