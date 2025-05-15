package com.example.vote.repository;

import com.example.vote.entity.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByPollId(Long pollId);

}
