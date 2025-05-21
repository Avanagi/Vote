package com.example.vote.repository;

import com.example.vote.entity.StudentPollEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentPollRepository extends JpaRepository<StudentPollEntity, Long> {
}
