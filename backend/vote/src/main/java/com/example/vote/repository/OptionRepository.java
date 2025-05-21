package com.example.vote.repository;

import com.example.vote.entity.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByPollId(Long pollId);


    Optional<OptionEntity> findById(Long optionId);
}
