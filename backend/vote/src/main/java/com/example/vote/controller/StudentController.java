package com.example.vote.controller;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.dto.StudentResponseDto;
import com.example.vote.exception.human.*;
import com.example.vote.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            StudentResponseDto student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (HumanNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Студент не найден.");
        }
    }

    @PostMapping("/loadStudent")
    public ResponseEntity<?> getStudent(@RequestBody Map<String, String> credentials) {
        try {
            StudentResponseDto student = studentService
                    .getStudentByEmailAndPassword(credentials.get("email"), credentials.get("password"));

            return ResponseEntity.ok(student);
        } catch (HumanNotFoundException | InvalidCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные учетные данные.");
        }
    }

    @PostMapping
    public ResponseEntity<?> saveStudent(@RequestBody StudentRegistrationDto dto) {
        try {
            studentService.saveStudent(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Студент сохранен");
        } catch (HumanEmailAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с таким email уже существует.");
        } catch (HumanSaveFailedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при сохранении студента: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudentById(@PathVariable Long id) {
        try {
            studentService.deleteStudentById(id);
            return ResponseEntity.ok("Студент успешно удалён.");
        } catch (HumanNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Студент не найден.");
        } catch (HumanDeleteFailedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении студента: " + ex.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateStudentById(@PathVariable Long id,
                                                    @RequestBody StudentResponseDto dto) {
        try {
            studentService.updateStudentById(id, dto);
            return ResponseEntity.ok("Данные студента успешно обновлены.");
        } catch (HumanNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Студент не найден.");
        } catch (HumanUpdateFailedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении студента: " + ex.getMessage());
        }
    }
}