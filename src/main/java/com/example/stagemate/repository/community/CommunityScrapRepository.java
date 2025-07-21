package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.community.CommunityScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityScrapRepository extends JpaRepository<CommunityScrap, Long> {
    boolean existsByUserIdAndCommunityPostId(Long userId, Long communityPostId);
    void deleteByUserIdAndCommunityPostId(Long userId, Long communityPostId);
    @Query("SELECT s.communityPost.id FROM CommunityScrap s WHERE s.user.id = :userId")
    List<Long>  findPostIdsByUserId(Long userId);

    @Query("SELECT s.communityPost FROM CommunityScrap s WHERE s.user.id = :userId ORDER BY s.id DESC")
    Page<CommunityPost> findScrappedPostsByUser(Long userId, Pageable pageable);

}