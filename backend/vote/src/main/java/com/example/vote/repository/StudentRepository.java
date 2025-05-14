package com.example.vote.repository;

import com.example.vote.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    StudentEntity findByEmail(String email);

    void deleteStudentEntityById(Long id);

}
