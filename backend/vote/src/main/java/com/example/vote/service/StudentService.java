package com.example.vote.service;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;

import java.util.List;

public interface StudentService {

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto getStudentByEmailAndPassword(String email, String password);

    void saveStudent(StudentRegistrationDto studentRegistrationDto);

    void deleteStudentById(Long id);

    void updateStudentById(Long id, StudentResponseDto studentResponseDto);

}
