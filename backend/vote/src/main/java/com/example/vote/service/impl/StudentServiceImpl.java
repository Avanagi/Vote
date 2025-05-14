package com.example.vote.service.impl;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;
import com.example.vote.entity.StudentEntity;
import com.example.vote.mapper.StudentRegistrationMapper;
import com.example.vote.mapper.StudentResponseMapper;
import com.example.vote.repository.StudentRepository;
import com.example.vote.service.SHA256HashingService;
import com.example.vote.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return studentResponseMapper.toDto(studentRepository.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentByEmailAndPassword(String email, String password) {
        log.info("Getting student by email and password: {}", email);
        StudentEntity studentEntity = studentRepository.findByEmail(email);
        if (studentEntity != null) {
            if (hashingService.validateSHA256Hash(password, studentEntity.getPassword())) {
                return studentResponseMapper.toDto(studentEntity);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public StudentRegistrationDto saveStudent(StudentRegistrationDto studentRegistrationDto) {
        log.info("Saving student: {}", studentRegistrationDto);
        StudentEntity studentEntity = studentRepository.save(studentRegisterMapper.toEntity(studentRegistrationDto));
        log.info("Student save successful: {}", studentEntity.getId());
        return studentRegisterMapper.toDto(studentEntity);
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        log.info("Deleting student by id: {}", id);
        studentRepository.deleteStudentEntityById(id);
    }

    @Override
    @Transactional
    public void updateStudentById(Long id, StudentResponseDto studentDTO) {
        log.info("Updating student by id: {}", id);
        studentResponseMapper.updateFromDto(studentDTO, studentRepository.findById(id).orElse(null));
    }
}



