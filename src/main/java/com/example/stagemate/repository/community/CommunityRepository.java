package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityCategory;
import com.example.stagemate.domain.community.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {

    // 삭제되지 않은 커뮤니티 게시글을 최신순으로 조회
    Page<CommunityPost> findAllByDeletedFalseAndCategoryOrderByCreatedAtDesc(CommunityCategory category, Pageable pageable);

    // 삭제되지 않고 카테고리가 일치하며 멤버 전용이 아닌 커뮤니티 게시글을 최신순으로 조회
    Page<CommunityPost> findAllByDeletedFalseAndCategoryAndMembersOnlyFalseOrderByCreatedAtDesc(CommunityCategory communityCategory, Pageable pageable);

    // 삭제되지 않은 커뮤니티 게시글을 최신순으로 조회
    Page<CommunityPost> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // 내가 작성한 삭제되지 않은 게시글 조회
    Page<CommunityPost> findAllByAuthorIdAndDeletedFalse(Long authorId, Pageable pageable);

}
