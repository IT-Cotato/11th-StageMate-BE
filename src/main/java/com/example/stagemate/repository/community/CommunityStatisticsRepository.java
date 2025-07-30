package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityStatisticsRepository extends JpaRepository<CommunityStatistics, Long> {
    // 커뮤니티 통계 정보를 업데이트하는 메서드
    // 24시간 내 작성된 커뮤니티 게시글에 대한 좋아요와 댓글 수를 합산하여 통계 정보를 업데이트
    @Modifying
    @Query(value = """
    INSERT INTO community_statistics (community_post_id, total_count, members_only)
    SELECT community_post_id,
           like_count + comment_count AS total_count,
           members_only
    FROM community_posts
    WHERE created_at >= CURRENT_TIMESTAMP - INTERVAL 24 HOUR
        AND deleted = false
    """, nativeQuery = true)
    void updateCommunityStatistics();


    @EntityGraph(attributePaths = {
            "communityPost",
            "communityPost.author"
    })
    Page<CommunityStatistics> findAllByOrderByTotalCountDesc(Pageable pageable);


    @EntityGraph(attributePaths = {
            "communityPost",
            "communityPost.author"
    })
    Page<CommunityStatistics> findAllByMembersOnlyFalseOrderByTotalCountDesc(Pageable pageable);

    // 기존 통계 정보를 삭제하는 메서드
    @Modifying
    @Query("DELETE FROM CommunityStatistics")
    void deleteAllStatistics();
}
