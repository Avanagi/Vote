package com.example.vote.service.impl;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;
import com.example.vote.entity.StudentEntity;
import com.example.vote.exception.human.*;
import com.example.vote.mapper.StudentRegistrationMapper;
import com.example.vote.mapper.StudentResponseMapper;
import com.example.vote.repository.StudentRepository;
import com.example.vote.service.HashingService;
import com.example.vote.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentResponseMapper studentResponseMapper;
    private final StudentRegistrationMapper studentRegisterMapper;
    private final HashingService hashingService;

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        log.debug("Получение списка всех студентов");
        return studentResponseMapper.toDtoList(studentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        log.debug("Получение студента по ID: {}", id);
        return studentResponseMapper.toDto(studentRepository
                .findById(id)
                .orElseThrow(() -> new HumanNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentByEmailAndPassword(String email, String password) {
        log.debug("Аутентификация студента по email: {}", email);
        StudentEntity studentEntity = studentRepository.findByEmail(email);
        if (studentEntity != null) {
            if (hashingService.validateSHA256Hash(password, studentEntity.getPassword())) {
                return studentResponseMapper.toDto(studentEntity);
            } else {
                log.warn("Неверный пароль для email: {}", email);
                throw new InvalidCredentialsException();
            }
        } else {
            log.warn("Студент с email {} не найден", email);
            throw new HumanNotFoundException("Студент с email " + email + " не найден.");
        }
    }

    @Override
    @Transactional
    public void saveStudent(StudentRegistrationDto studentRegistrationDto) {
        log.debug("Сохранение нового студента с email: {}", studentRegistrationDto.getEmail());
        try {
            if (studentRepository.existsByEmail(studentRegistrationDto.getEmail())) {
                log.warn("Студент с email {} уже существует", studentRegistrationDto.getEmail());
                throw new HumanEmailAlreadyExistsException(studentRegistrationDto.getEmail());
            }
            String passwordHash = hashingService.generateSHA256Hash(studentRegistrationDto.getPassword());
            StudentEntity studentEntity = studentRegisterMapper.toEntity(studentRegistrationDto);
            studentEntity.setPassword(passwordHash);
            StudentEntity savedStudent = studentRepository.save(studentEntity);
            log.info("Студент успешно сохранён с ID: {}", savedStudent.getId());
            studentRegisterMapper.toDto(savedStudent);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении студента из-за нарушения целостности данных: {}", studentRegistrationDto.getEmail(), e);
            throw new HumanSaveFailedException("Не удалось сохранить студента из-за нарушения целостности данных.", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении студента: {}", studentRegistrationDto.getEmail(), e);
            throw new HumanSaveFailedException("Не удалось сохранить студента.", e);
        }
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        log.info("Удаление студента по ID: {}", id);
        try {
            if (!studentRepository.existsById(id)) {
                log.warn("Студент с ID {} не найден для удаления", id);
                throw new HumanNotFoundException(id);
            }
            studentRepository.deleteStudentEntityById(id);
            log.info("Студент с ID {} успешно удалён", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении студента с ID {}: {}", id, e.getMessage());
            throw new HumanDeleteFailedException(id.toString(), e);
        }
    }

    @Override
    @Transactional
    public void updateStudentById(Long id, StudentResponseDto studentDTO) {
        log.info("Обновление данных студента по ID: {}", id);
        StudentEntity existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new HumanNotFoundException(id));
        try {
            studentResponseMapper.updateFromDto(studentDTO, existingStudent);
            studentRepository.save(existingStudent);
            log.info("Студент с ID {} успешно обновлён", id);
        } catch (Exception e) {
            log.error("Ошибка при обновлении студента с ID {}: {}", id, e.getMessage());
            throw new HumanUpdateFailedException(id.toString(), e);
        }
    }
}