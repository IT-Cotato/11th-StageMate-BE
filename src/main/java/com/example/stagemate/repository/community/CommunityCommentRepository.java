package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.domain.community.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    @Query("SELECT c FROM CommunityComment c LEFT JOIN FETCH c.children WHERE c.post = :post AND c.parent IS NULL ORDER BY c.createdAt ASC")
    List<CommunityComment> findRootCommentsWithChildrenByPost(@Param("post") CommunityPost post);

    // 작성자가 작성한 댓글을 포함한 게시글 목록 조회
    @Query("""
        SELECT c.post
        FROM CommunityComment c
        WHERE c.user.id = :userId AND c.post.deleted = false
        GROUP BY c.post
        ORDER BY MAX(c.createdAt) DESC
    """)
    Page<CommunityPost> findDistinctPostsByWriterId(@Param("userId") Long userId, Pageable pageable);

}
