package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityPost;

import java.time.LocalDateTime;

public record CommunityPostListResponse(
        Long id,
        String title,
        String category, // 일상, 꿀팁, 나눔거래
        String author,
        LocalDateTime createdAt,
        int viewCount,
        int likeCount,
        int commentCount
) {
    public static CommunityPostListResponse from(CommunityPost post) {
        return new CommunityPostListResponse(
                post.getId(),
                post.getTitle(),
                post.getCategory().getDescription(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }
}
