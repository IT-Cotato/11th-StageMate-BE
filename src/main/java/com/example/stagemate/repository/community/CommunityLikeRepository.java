package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    boolean existsByUserIdAndCommunityPostId(Long userId, Long communityPostId);
    void deleteByUserIdAndCommunityPostId(Long userId, Long communityPostId);
    @Query("SELECT s.communityPost.id FROM CommunityLike s WHERE s.user.id = :userId")
    List<Long> findPostIdsByUserId(Long userId);
}