package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityPost;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommunityPostResponse {
    private Long id;
    String title;
    JsonNode content;
    String authorName;
    LocalDateTime createdAt;
    int viewCount;
    String category; // 일상, 나눔거래, 꿀팁
    String tradeCategory; // 나눔거래일때 카테고리(연극, 뮤지컬)
    String tradeMethod; // 나눔거래일떄 방법(추첨나눔/판매/선착나눔)
    List<CommunityImageResponse> imageUrls; // 나중에 image 변경해야할 때 id 필요해서 entity로 변경
    int likeCount;
    int commentCount;
    int scrapCount;
    boolean isScrapped;
    boolean isLiked;
    boolean membersOnly;
    List<CommunityCommentResponse> comments; // 댓글 목록

    public static CommunityPostResponse from(CommunityPost post, boolean isScrapped, boolean isLiked, JsonNode jsonContent, List<CommunityCommentResponse> commentsList) {
        return new CommunityPostResponse(
                post.getId(),
                post.getTitle(),
                jsonContent,
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getCategory().getDescription(),
                post.getTradeCategory() == null ? null : post.getTradeCategory().getDescription(),
                post.getTradeMethod() == null ? null : post.getTradeMethod().getDescription(),
                post.getImages().stream()
                        .map(CommunityImageResponse::from)
                        .toList(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getScrapCount(),
                isScrapped,
                isLiked,
                post.isMembersOnly(),
                commentsList
        );
    }
}
