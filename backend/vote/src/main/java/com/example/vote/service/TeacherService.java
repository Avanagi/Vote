package com.example.vote.service;

import com.example.vote.dto.TeacherRegistrationDto;
import com.example.vote.dto.TeacherResponseDto;

import java.util.List;

public interface TeacherService {

    List<TeacherResponseDto> getAllTeachers();

    TeacherResponseDto getTeacherById(Long id);

    TeacherResponseDto getTeacherByEmailAndPassword(String email, String password);

    TeacherRegistrationDto saveTeacher(TeacherRegistrationDto teacherRegistrationDto);

    void deleteTeacherById(Long id);

    void updateTeacherById(Long id, TeacherResponseDto teacherResponseDto);

}
