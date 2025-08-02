package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.community.CommunityStatistics;

import java.time.LocalDateTime;

public record CommunityPostListResponse(
        Long id,
        String title,
        String category, // 일상, 꿀팁, 나눔거래
        String author,
        LocalDateTime createdAt,
        int viewCount,
        int likeCount,
        int commentCount,
        boolean isLiked // 현재 로그인 사용자가 이 글을 좋아요 눌렀는지 여부
) {
    public static CommunityPostListResponse from(CommunityPost post, boolean isLiked) {
        return new CommunityPostListResponse(
                post.getId(),
                post.getTitle(),
                post.getCategory().getDescription(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                isLiked
        );
    }

    public static CommunityPostListResponse masked(CommunityPost post, boolean isLiked) {
        return new CommunityPostListResponse(
                post.getId(),
                "차단한 사용자의 게시물입니다.",
                post.getCategory().getDescription(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                isLiked
        );
    }

    public static CommunityPostListResponse fromStat(CommunityStatistics stat, boolean isLiked) {
        CommunityPost post = stat.getCommunityPost();
        return new CommunityPostListResponse(
                post.getId(),
                post.getTitle(),
                post.getCategory().getDescription(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                isLiked
        );
    }

    public static CommunityPostListResponse maskedStat(CommunityStatistics stat, boolean isLiked) {
        CommunityPost post = stat.getCommunityPost();
        return new CommunityPostListResponse(
                post.getId(),
                "차단한 사용자의 게시물입니다.",
                post.getCategory().getDescription(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                isLiked
        );
    }
}
