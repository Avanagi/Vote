package com.example.blockchain.repository;

import com.example.blockchain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByPollId(Long pollId);

    List<TransactionEntity> findByStudentId(Long studentId);
}