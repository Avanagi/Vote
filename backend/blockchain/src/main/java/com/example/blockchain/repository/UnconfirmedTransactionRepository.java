package com.example.blockchain.repository;

import com.example.blockchain.entity.UnconfirmedTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnconfirmedTransactionRepository extends JpaRepository<UnconfirmedTransactionEntity, Long> {
    void deleteByPollIdAndStudentId(Long pollId, Long studentId);
}
