package com.example.vote.controller;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;
import com.example.vote.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        log.info("Get all students");
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentResponseDto getStudentById(@PathVariable Long id) {
        log.info("Get student by id: {}", id);
        return studentService.getStudentById(id);
    }

    @PostMapping("/loadStudent")
    public StudentResponseDto getStudent(@RequestBody Map<String, String> credentials) {
        log.info("Get student by credentials");
        return studentService.getStudentByEmailAndPassword(credentials.get("email"), credentials.get("password"));
    }

    @PostMapping
    public StudentRegistrationDto saveStudent(@RequestBody StudentRegistrationDto studentRegistrationDto) {
        log.info("Received request: {}", studentRegistrationDto);
        return studentService.saveStudent(studentRegistrationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteStudentById(@PathVariable Long id) {
        log.info("Delete student by id: {}", id);
        studentService.deleteStudentById(id);
    }

    @PostMapping("/update/{id}")
    public void updateStudentById(@PathVariable Long id, @RequestBody StudentResponseDto studentDTO) {
        log.info("Update student by id: {}. Parameters: {}", id, studentDTO);
        studentService.updateStudentById(id, studentDTO);
    }

}

