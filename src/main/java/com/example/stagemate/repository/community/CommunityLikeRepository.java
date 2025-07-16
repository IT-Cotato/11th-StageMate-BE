package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    boolean existsByUserIdAndCommunityPostId(Long userId, Long communityPostId);
    void deleteByUserIdAndCommunityPostId(Long userId, Long communityPostId);
}