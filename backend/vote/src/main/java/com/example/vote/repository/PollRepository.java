package com.example.vote.repository;

import com.example.vote.entity.PollEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PollRepository extends JpaRepository<PollEntity, Long> {

    @Query("SELECT p FROM PollEntity p " +
            "WHERE (p.visibleFor IS NULL OR " +
            "   p.visibleFor = :groupName OR " +
            "   p.visibleFor LIKE CONCAT(:groupName, ',%') OR " +
            "   p.visibleFor LIKE CONCAT('%,', :groupName, ',%') OR " +
            "   p.visibleFor LIKE CONCAT('%,', :groupName)) " +
            "AND NOT EXISTS (" +
            "   SELECT up FROM StudentPollEntity up " +
            "   WHERE up.poll.id = p.id AND up.student.id = :userId)")
    List<PollEntity> findAvailablePollsForUserAndGroup(@Param("userId") Long userId,
                                                       @Param("groupName") String groupName);

}
