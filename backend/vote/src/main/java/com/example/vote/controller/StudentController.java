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

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        log.info("Get all students");
        return studentService.getAllStudents();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/{id}")
    public StudentResponseDto getStudentById(@PathVariable Long id) {
        log.info("Get student by id: {}", id);
        return studentService.getStudentById(id);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/loadStudent")
    public StudentResponseDto getStudent(@RequestBody Map<String, String> credentials) {
        log.info("Get student by credentials");
        return studentService.getStudentByEmailAndPassword(credentials.get("email"), credentials.get("password"));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping
    public StudentRegistrationDto saveStudent(@RequestBody StudentRegistrationDto studentRegistrationDto) {
        log.info("Received request: {}", studentRegistrationDto);
        return studentService.saveStudent(studentRegistrationDto);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping("/{id}")
    public void deleteStudentById(@PathVariable Long id) {
        log.info("Delete student by id: {}", id);
        studentService.deleteStudentById(id);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/update/{id}")
    public void updateStudentById(@PathVariable Long id, @RequestBody StudentResponseDto studentDTO) {
        log.info("Update student by id: {}. Parameters: {}", id, studentDTO);
        studentService.updateStudentById(id, studentDTO);
    }

}

