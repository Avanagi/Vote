package com.example.vote.controller;

import com.example.vote.dto.TeacherRegistrationDto;
import com.example.vote.dto.TeacherResponseDto;
import com.example.vote.exception.human.*;
import com.example.vote.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponseDto>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Long id) {
        try {
            TeacherResponseDto teacher = teacherService.getTeacherById(id);
            return ResponseEntity.ok(teacher);
        } catch (HumanNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Преподаватель не найден.");
        }
    }

    @PostMapping("/loadTeacher")
    public ResponseEntity<?> getTeacher(@RequestBody Map<String, String> credentials) {
        try {
            TeacherResponseDto teacher = teacherService
                    .getTeacherByEmailAndPassword(credentials.get("email"), credentials.get("password"));
            return ResponseEntity.ok(teacher);
        } catch (HumanNotFoundException | InvalidCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные учетные данные.");
        }
    }

    @PostMapping
    public ResponseEntity<?> saveTeacher(@RequestBody TeacherRegistrationDto dto) {
        try {
            teacherService.saveTeacher(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Преподаватель успешно сохранен.");
        } catch (HumanEmailAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с таким email уже существует.");
        } catch (HumanSaveFailedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при сохранении преподавателя: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeacherById(@PathVariable Long id) {
        try {
            teacherService.deleteTeacherById(id);
            return ResponseEntity.ok("Преподаватель успешно удалён.");
        } catch (HumanNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Преподаватель не найден.");
        } catch (HumanDeleteFailedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении преподавателя: " + ex.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateTeacherById(@PathVariable Long id,
                                                    @RequestBody TeacherResponseDto dto) {
        try {
            teacherService.updateTeacherById(id, dto);
            return ResponseEntity.ok("Данные преподавателя успешно обновлены.");
        } catch (HumanNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Преподаватель не найден.");
        } catch (HumanUpdateFailedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении преподавателя: " + ex.getMessage());
        }
    }
}
