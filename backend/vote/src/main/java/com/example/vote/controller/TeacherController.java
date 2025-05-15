package com.example.vote.controller;

import com.example.vote.dto.TeacherRegistrationDto;
import com.example.vote.dto.TeacherResponseDto;
import com.example.vote.exception.human.*;
import com.example.vote.service.StudentService;
import com.example.vote.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/teachers")
@CrossOrigin(origins = "http://localhost:5173")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<TeacherResponseDto> getAllTeachers() {
        log.info("Get all teachers");
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> getTeacherById(@PathVariable Long id) {
        log.info("Get teacher by id: {}", id);
        try {
            TeacherResponseDto teacherResponseDto = teacherService.getTeacherById(id);
            return new ResponseEntity<>(teacherResponseDto, HttpStatus.OK);
        } catch (HumanNotFoundException ex) {
            log.error("Teacher not found: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/loadTeacher")
    public ResponseEntity<TeacherResponseDto> getTeacher(@RequestBody Map<String, String> credentials) {
        log.info("Get teacher by credentials");
        try {
            TeacherResponseDto teacherResponseDto = teacherService
                    .getTeacherByEmailAndPassword(credentials.get("email"), credentials.get("password"));
            if (teacherResponseDto != null) {
                return new ResponseEntity<>(teacherResponseDto, HttpStatus.OK);
            } else {
                throw new InvalidCredentialsException();
            }
        } catch (HumanNotFoundException | InvalidCredentialsException ex) {
            log.error("Authentication failed: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<TeacherRegistrationDto> saveTeacher(@RequestBody TeacherRegistrationDto teacherRegistrationDto) {
        log.info("Received request: {}", teacherRegistrationDto);
        try {
            TeacherRegistrationDto savedTeacher = teacherService.saveTeacher(teacherRegistrationDto);
            return new ResponseEntity<>(savedTeacher, HttpStatus.CREATED);
        } catch (HumanEmailAlreadyExistsException ex) {
            log.error("Email already exists: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (HumanSaveFailedException ex) {
            log.error("Failed to save teacher: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacherById(@PathVariable Long id) {
        log.info("Delete teacher by id: {}", id);
        try {
            teacherService.deleteTeacherById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (HumanNotFoundException ex) {
            log.error("Teacher not found for deletion: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (HumanDeleteFailedException ex) {
            log.error("Failed to delete teacher: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateTeacherById(@PathVariable Long id, @RequestBody TeacherResponseDto teacherResponseDto) {
        log.info("Update teacher by id: {}. Parameters: {}", id, teacherResponseDto);
        try {
            teacherService.updateTeacherById(id, teacherResponseDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HumanNotFoundException ex) {
            log.error("Teacher not found for update: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (HumanUpdateFailedException ex) {
            log.error("Failed to update teacher: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
