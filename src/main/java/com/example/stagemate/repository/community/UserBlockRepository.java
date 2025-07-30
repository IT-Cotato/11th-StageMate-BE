package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.UserBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    Boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    Page<UserBlock> findAllByBlockerId(Long blockerId, Pageable pageable);
    List<UserBlock> findAllByBlockerId(Long blockerId);
    @Query("SELECT ub.blocked.id FROM UserBlock ub WHERE ub.blocker.id = :blockerId")
    Set<Long> findBlockedUserIdsByBlockerId(@Param("blockerId") Long blockerId);
}
