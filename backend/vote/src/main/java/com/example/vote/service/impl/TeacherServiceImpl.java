package com.example.vote.service.impl;

import com.example.vote.dto.TeacherRegistrationDto;
import com.example.vote.dto.TeacherResponseDto;
import com.example.vote.entity.TeacherEntity;
import com.example.vote.exception.human.*;
import com.example.vote.mapper.TeacherRegistrationMapper;
import com.example.vote.mapper.TeacherResponseMapper;
import com.example.vote.repository.TeacherRepository;
import com.example.vote.service.HashingService;
import com.example.vote.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherResponseMapper teacherResponseMapper;
    private final TeacherRegistrationMapper teacherRegistrationMapper;
    private final HashingService hashingService;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getAllTeachers() {
        log.debug("Получение списка всех преподавателей");
        return teacherResponseMapper.toDtoList(teacherRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherById(Long id) {
        log.debug("Получение преподавателя по ID: {}", id);
        return teacherResponseMapper.toDto(teacherRepository
                .findById(id)
                .orElseThrow(() -> new HumanNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherByEmailAndPassword(String email, String password) {
        log.debug("Аутентификация преподавателя по email: {}", email);
        TeacherEntity teacherEntity = teacherRepository.findByEmail(email);
        if (teacherEntity != null) {
            if (hashingService.validateSHA256Hash(password, teacherEntity.getPassword())) {
                return teacherResponseMapper.toDto(teacherEntity);
            } else {
                log.warn("Неверный пароль для преподавателя с email: {}", email);
                throw new InvalidCredentialsException();
            }
        } else {
            log.warn("Преподаватель с email {} не найден", email);
            throw new HumanNotFoundException("Преподаватель с email " + email + " не найден.");
        }
    }

    @Override
    @Transactional
    public void saveTeacher(TeacherRegistrationDto teacherRegistrationDto) {
        log.debug("Сохранение нового преподавателя с email: {}", teacherRegistrationDto.getEmail());
        try {
            if (teacherRepository.existsByEmail(teacherRegistrationDto.getEmail())) {
                log.warn("Преподаватель с email {} уже существует", teacherRegistrationDto.getEmail());
                throw new HumanEmailAlreadyExistsException(teacherRegistrationDto.getEmail());
            }
            String passwordHash = hashingService.generateSHA256Hash(teacherRegistrationDto.getPassword());
            TeacherEntity teacherEntity = teacherRegistrationMapper.toEntity(teacherRegistrationDto);
            teacherEntity.setPassword(passwordHash);
            TeacherEntity savedTeacher = teacherRepository.save(teacherEntity);
            log.info("Преподаватель успешно сохранён с ID: {}", savedTeacher.getId());
            teacherRegistrationMapper.toDto(savedTeacher);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении преподавателя из-за нарушения целостности данных: {}", teacherRegistrationDto.getEmail(), e);
            throw new HumanSaveFailedException("Не удалось сохранить преподавателя из-за нарушения целостности данных.", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении преподавателя: {}", teacherRegistrationDto.getEmail(), e);
            throw new HumanSaveFailedException("Не удалось сохранить преподавателя.", e);
        }
    }

    @Override
    @Transactional
    public void deleteTeacherById(Long id) {
        log.info("Удаление преподавателя по ID: {}", id);
        try {
            if (!teacherRepository.existsById(id)) {
                log.warn("Преподаватель с ID {} не найден для удаления", id);
                throw new HumanNotFoundException(id);
            }
            teacherRepository.deleteTeacherEntityById(id);
            log.info("Преподаватель с ID {} успешно удалён", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении преподавателя с ID {}: {}", id, e.getMessage());
            throw new HumanDeleteFailedException(id.toString(), e);
        }
    }

    @Override
    @Transactional
    public void updateTeacherById(Long id, TeacherResponseDto teacherResponseDto) {
        log.info("Обновление данных преподавателя по ID: {}", id);
        TeacherEntity existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new HumanNotFoundException(id));
        try {
            teacherResponseMapper.updateFromDto(teacherResponseDto, existingTeacher);
            teacherRepository.save(existingTeacher);
            log.info("Преподаватель с ID {} успешно обновлён", id);
        } catch (Exception e) {
            log.error("Ошибка при обновлении преподавателя с ID {}: {}", id, e.getMessage());
            throw new HumanUpdateFailedException(id.toString(), e);
        }
    }
}