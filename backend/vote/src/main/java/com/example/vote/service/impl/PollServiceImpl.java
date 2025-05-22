package com.example.vote.service.impl;

import com.example.vote.dto.OptionDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public PollDto createPoll(PollDto pollDTO) {
        log.info("Creating poll: {}", pollDTO.getQuestion());
        PollEntity pollEntity = new PollEntity();
        pollEntity.setQuestion(pollDTO.getQuestion());
        pollEntity.setOptions(new ArrayList<>());

        // Сохраняем список групп как строку, или null
        if (pollDTO.getVisibleFor() == null || pollDTO.getVisibleFor().isEmpty()) {
            pollEntity.setVisibleFor(null);
        } else {
            String visibleForStr = String.join(",", pollDTO.getVisibleFor());
            pollEntity.setVisibleFor(visibleForStr);
        }
        pollEntity.setTeacherId(pollDTO.getTeacherId());

        pollEntity.setCreatedAt(LocalDateTime.now());

        try {
            PollEntity savedPollEntity = pollRepository.save(pollEntity);
            log.info("Poll created successfully with ID: {}", savedPollEntity.getId());

            // Сохраняем опции
            List<OptionEntity> optionEntities = new ArrayList<>();
            if (pollDTO.getOptions() != null && !pollDTO.getOptions().isEmpty()) {
                for (OptionDto optionDTO : pollDTO.getOptions()) {
                    OptionEntity optionEntity = new OptionEntity();
                    optionEntity.setOptionText(optionDTO.getOptionText());
                    optionEntity.setPoll(savedPollEntity);
                    optionEntities.add(optionEntity);
                }
                optionRepository.saveAll(optionEntities);
                savedPollEntity.setOptions(optionEntities);
            }

            // Отправка уведомлений
            List<StudentEntity> targetStudents;
            if (pollEntity.getVisibleFor() == null || pollEntity.getVisibleFor().isBlank()) {
                log.info("Poll is public. Sending notifications to all students.");
                targetStudents = studentRepository.findAll();
            } else {
                List<String> groups = Arrays.stream(pollEntity.getVisibleFor().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                log.info("Poll is visible to groups: {}", groups);
                targetStudents = studentRepository.findByStudentGroupIn(groups);
            }

            sendEmailsToStudents(targetStudents, savedPollEntity);

            return pollMapper.toDto(savedPollEntity);

        } catch (DataAccessException e) {
            log.error("Error during poll creation: {}", pollDTO.getQuestion(), e);
            throw new PollCreationException("Failed to create poll due to database error.", e);
        }
    }


    private void sendEmailsToStudents(List<StudentEntity> students, PollEntity pollEntity) {
        if (students == null || students.isEmpty()) {
            log.info("No users to notify for poll ID: {}", pollEntity.getId());
            return;
        }

        for (StudentEntity student : students) {
            try {
                emailService.sendEmail(
                        student.getEmail(),
                        "Доступен новый опрос: " + pollEntity.getQuestion(),
                        "Пожалуйста, перейдите по ссылке, чтобы принять участие: http://localhost:8080/polls/" + pollEntity.getId()
                );
                log.info("Email notification sent to {} for poll ID: {}", student.getEmail(), pollEntity.getId());
            } catch (Exception e) {
                log.error("Failed to send email to {} for poll ID: {}", student.getEmail(), pollEntity.getId(), e);
            }
        }
    }



    @Override
    @Transactional(readOnly = true)
    public PollDto getPollById(Long id) {
        log.info("Getting poll by ID: {}", id);
        PollEntity pollEntity = pollRepository.findById(id)
                .orElseThrow(() -> new PollNotFoundException(id));
        return pollMapper.toDtoWithOption(pollEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PollDto> getAllPolls() {
        log.info("Getting all polls");
        return pollRepository.findAll().stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePoll(Long id) {
        log.info("Deleting poll with ID: {}", id);
        try {
            if (!pollRepository.existsById(id)) {
                throw new PollNotFoundException(id);
            }
            pollRepository.deleteById(id);
            log.info("Poll with ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            log.error("Error during poll deletion with ID {}: {}", id, e.getMessage());
            throw new PollDeletionException(id.toString(), e);
        }
    }

    @Transactional
    public List<PollDto> getAvailablePollsForUserAndGroup(Long userId,  String group) {
        log.info("Getting available polls for user ID: {}", userId);
        return pollRepository.findAvailablePollsForUserAndGroup(userId, group).stream()
                .map(pollMapper::toDtoWithOption)
                .collect(Collectors.toList());
    }
}

