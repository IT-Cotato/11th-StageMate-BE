package com.example.stagemate.dto.request.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.user.entity.UserJpaEntity;

public record CommunityCommentRequest(
        Long parentId, // null 이면 댓글, 있으면 대댓글
        String content
) {
    public CommunityComment toEntity(CommunityPost post, UserJpaEntity user, CommunityComment parent) {
        return CommunityComment.builder()
                .content(content())
                .post(post)
                .user(user)
                .parent(parentId() != null ? parent : null)
                .build();
    }

}
