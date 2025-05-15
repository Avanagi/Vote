package com.example.blockchain.repository;

import com.example.blockchain.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
    Optional<BlockEntity> findTopByOrderByIndexDesc();

    List<BlockEntity> findAllByOrderByIndexAsc();
}
