package com.example.vote.service.impl;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;
import com.example.vote.entity.StudentEntity;
import com.example.vote.exception.student.*;
import com.example.vote.mapper.StudentRegistrationMapper;
import com.example.vote.mapper.StudentResponseMapper;
import com.example.vote.repository.StudentRepository;
import com.example.vote.service.SHA256HashingService;
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
    private final SHA256HashingService hashingService;

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        log.info("Getting all students");
        return studentResponseMapper.toDtoList(studentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        log.info("Getting student by id: {}", id);
        return studentResponseMapper.toDto(studentRepository
                .findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentByEmailAndPassword(String email, String password) {
        log.info("Getting student by email and password: {}", email);
        StudentEntity studentEntity = studentRepository.findByEmail(email);
        if (studentEntity != null) {
            if (hashingService.validateSHA256Hash(password, studentEntity.getPassword())) {
                return studentResponseMapper.toDto(studentEntity);
            } else {
                throw new InvalidCredentialsException();
            }
        } else {
            throw new StudentNotFoundException("Student with email " + email + " not found.");
        }
    }

    @Override
    @Transactional
    public StudentRegistrationDto saveStudent(StudentRegistrationDto studentRegistrationDto) {
        log.info("Saving student: {}", studentRegistrationDto.getEmail());
        try {
            if (studentRepository.existsByEmail(studentRegistrationDto.getEmail())) {
                throw new StudentEmailAlreadyExistsException(studentRegistrationDto.getEmail());
            }
            String passwordHash = hashingService.generateSHA256Hash(studentRegistrationDto.getPassword());
            StudentEntity studentEntity = studentRegisterMapper.toEntity(studentRegistrationDto);
            studentEntity.setPassword(passwordHash);
            StudentEntity savedStudent = studentRepository.save(studentEntity);
            log.info("Student save successful: {}", savedStudent.getId());
            return studentRegisterMapper.toDto(savedStudent);
        } catch (DataIntegrityViolationException e) {
            log.error("Error saving student due to data integrity violation: {}", studentRegistrationDto.getEmail(), e);
            throw new StudentSaveFailedException("Could not save student due to data integrity constraints.", e);
        } catch (Exception e) {
            log.error("Unexpected error during student save: {}", studentRegistrationDto.getEmail(), e);
            throw new StudentSaveFailedException("Failed to save student.", e);
        }
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        log.info("Deleting student by id: {}", id);
        try {
            if (!studentRepository.existsById(id)) {
                throw new StudentNotFoundException(id);
            }
            studentRepository.deleteStudentEntityById(id);
            log.info("Student with id {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Error deleting student with id {}: {}", id, e.getMessage());
            throw new StudentDeleteFailedException(id.toString(), e);
        }
    }

    @Override
    @Transactional
    public void updateStudentById(Long id, StudentResponseDto studentDTO) {
        log.info("Updating student by id: {}", id);
        StudentEntity existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        try {
            studentResponseMapper.updateFromDto(studentDTO, existingStudent);
            studentRepository.save(existingStudent);
            log.info("Student with id {} updated successfully.", id);
        } catch (Exception e) {
            log.error("Error updating student with id {}: {}", id, e.getMessage());
            throw new StudentUpdateFailedException(id.toString(), e);
        }
    }
}