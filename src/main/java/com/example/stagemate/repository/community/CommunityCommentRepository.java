package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.domain.community.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    @Query("SELECT c FROM CommunityComment c LEFT JOIN FETCH c.children WHERE c.post = :post AND c.parent IS NULL ORDER BY c.createdAt ASC")
    List<CommunityComment> findRootCommentsWithChildrenByPost(@Param("post") CommunityPost post);
}
