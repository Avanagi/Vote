package com.example.blockchain.repository;

import com.example.blockchain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByPollId(Long pollId);
}