package com.example.vote.service.impl;

import com.example.vote.dto.TeacherRegistrationDto;
import com.example.vote.dto.TeacherResponseDto;
import com.example.vote.entity.StudentEntity;
import com.example.vote.entity.TeacherEntity;
import com.example.vote.exception.human.*;
import com.example.vote.mapper.TeacherRegistrationMapper;
import com.example.vote.mapper.TeacherResponseMapper;
import com.example.vote.repository.TeacherRepository;
import com.example.vote.service.SHA256HashingService;
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
    private final SHA256HashingService sha256HashingService;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getAllTeachers() {
        log.info("Getting all teachers");
        return teacherResponseMapper.toDtoList(teacherRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherById(Long id) {
        log.info("Getting teacher by id: {}", id);
        return teacherResponseMapper.toDto(teacherRepository
                .findById(id)
                .orElseThrow(() -> new HumanNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherByEmailAndPassword(String email, String password) {
        log.info("Getting teacher by email and password: {}", email);
        TeacherEntity teacherEntity = teacherRepository.findByEmail(email);
        if (teacherEntity != null) {
            if (sha256HashingService.validateSHA256Hash(password, teacherEntity.getPassword())) {
                return teacherResponseMapper.toDto(teacherEntity);
            } else {
                throw new InvalidCredentialsException();
            }
        } else {
            throw new HumanNotFoundException("Student with email " + email + " not found.");
        }
    }

    @Override
    @Transactional
    public TeacherRegistrationDto saveTeacher(TeacherRegistrationDto teacherRegistrationDto) {
        log.info("Saving teacher: {}", teacherRegistrationDto.getEmail());
        try {
            if (teacherRepository.existsByEmail(teacherRegistrationDto.getEmail())) {
                throw new HumanEmailAlreadyExistsException(teacherRegistrationDto.getEmail());
            }
            String passwordHash = sha256HashingService.generateSHA256Hash(teacherRegistrationDto.getPassword());
            TeacherEntity teacherEntity = teacherRegistrationMapper.toEntity(teacherRegistrationDto);
            teacherEntity.setPassword(passwordHash);
            TeacherEntity savedTeacher = teacherRepository.save(teacherEntity);
            log.info("Teacher save successful: {}", savedTeacher.getId());
            return teacherRegistrationMapper.toDto(savedTeacher);
        } catch (DataIntegrityViolationException e) {
            log.error("Error saving teacher due to data integrity violation: {}", teacherRegistrationDto.getEmail(), e);
            throw new HumanSaveFailedException("Could not save teacher due to data integrity constraints.", e);
        } catch (Exception e) {
            log.error("Unexpected error during teacher save: {}", teacherRegistrationDto.getEmail(), e);
            throw new HumanSaveFailedException("Failed to save teacher.", e);
        }
    }

    @Override
    @Transactional
    public void deleteTeacherById(Long id) {
        log.info("Deleting teacher by id: {}", id);
        try {
            if (!teacherRepository.existsById(id)) {
                throw new HumanNotFoundException(id);
            }
            teacherRepository.deleteTeacherEntityById(id);
            log.info("Teacher with id {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Error deleting teacher with id {}: {}", id, e.getMessage());
            throw new HumanDeleteFailedException(id.toString(), e);
        }
    }

    @Override
    @Transactional
    public void updateTeacherById(Long id, TeacherResponseDto teacherResponseDto) {
        log.info("Updating teacher by id: {}", id);
        TeacherEntity existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new HumanNotFoundException(id));
        try {
            teacherResponseMapper.updateFromDto(teacherResponseDto, existingTeacher);
            teacherRepository.save(existingTeacher);
            log.info("Teacher with id {} updated successfully.", id);
        } catch (Exception e) {
            log.error("Error updating teacher with id {}: {}", id, e.getMessage());
            throw new HumanUpdateFailedException(id.toString(), e);
        }
    }
}
