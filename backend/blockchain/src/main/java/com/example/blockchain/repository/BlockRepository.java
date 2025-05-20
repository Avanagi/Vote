package com.example.blockchain.repository;

import com.example.blockchain.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
    Optional<BlockEntity> findTopByOrderByIndexDesc();

    List<BlockEntity> findAllByOrderByIndexAsc();
}
