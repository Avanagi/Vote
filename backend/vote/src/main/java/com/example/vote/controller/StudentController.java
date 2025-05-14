package com.example.vote.controller;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;
import com.example.vote.exception.student.*;
import com.example.vote.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        log.info("Get all students");
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        log.info("Get student by id: {}", id);
        try {
            StudentResponseDto student = studentService.getStudentById(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (StudentNotFoundException ex) {
            log.error("Student not found: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/loadStudent")
    public ResponseEntity<StudentResponseDto> getStudent(@RequestBody Map<String, String> credentials) {
        log.info("Get student by credentials");
        try {
            StudentResponseDto student = studentService
                    .getStudentByEmailAndPassword(credentials.get("email"), credentials.get("password"));
            if (student != null) {
                return new ResponseEntity<>(student, HttpStatus.OK);
            } else {
                throw new InvalidCredentialsException();
            }
        } catch (StudentNotFoundException | InvalidCredentialsException ex) {
            log.error("Authentication failed: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<StudentRegistrationDto> saveStudent(@RequestBody StudentRegistrationDto studentRegistrationDto) {
        log.info("Received request: {}", studentRegistrationDto);
        try {
            StudentRegistrationDto savedStudent = studentService.saveStudent(studentRegistrationDto);
            return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
        } catch (StudentEmailAlreadyExistsException ex) {
            log.error("Email already exists: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (StudentSaveFailedException ex) {
            log.error("Failed to save student: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable Long id) {
        log.info("Delete student by id: {}", id);
        try {
            studentService.deleteStudentById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (StudentNotFoundException ex) {
            log.error("Student not found for deletion: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (StudentDeleteFailedException ex) {
            log.error("Failed to delete student: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateStudentById(@PathVariable Long id, @RequestBody StudentResponseDto studentDTO) {
        log.info("Update student by id: {}. Parameters: {}", id, studentDTO);
        try {
            studentService.updateStudentById(id, studentDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StudentNotFoundException ex) {
            log.error("Student not found for update: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (StudentUpdateFailedException ex) {
            log.error("Failed to update student: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

