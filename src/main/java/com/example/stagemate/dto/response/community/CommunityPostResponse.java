package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommunityPostResponse {
    private Long id;
    String title;
    String content;
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

    public static CommunityPostResponse from(CommunityPost post, boolean isScrapped, boolean isLiked) {
        return new CommunityPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getCategory().name(),
                post.getTradeCategory().toString(),
                post.getTradeMethod().toString(),
                post.getImages().stream()
                        .map(CommunityImageResponse::from)
                        .toList(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getScrapCount(),
                isScrapped,
                isLiked,
                post.isMembersOnly()
        );
    }
}
