package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityPost;

public record CommunityPostTradeListResponse(
        Long id,
        String title,
        String category, // 일상, 꿀팁, 나눔거래
        String tradeCategory, // 나눔거래일때 카테고리(연극, 뮤지컬)
        String tradeMethod, // 나눔거래일때 방법(추첨나눔, 판매, 선착나눔)
        boolean isScrapped, // 현재 로그인 사용자가 이 글을 스크랩했는지 여부
        String imageUrl

) {
    public static CommunityPostTradeListResponse from(CommunityPost post, boolean isScrapped) {
        return new CommunityPostTradeListResponse(
                post.getId(),
                post.getTitle(),
                post.getCategory().getDescription(),
                post.getTradeCategory().getDescription(),
                post.getTradeMethod().getDescription(),
                isScrapped,
                post.getImages().isEmpty() ? "basic" : post.getImages().get(0).getImage().getImageUrl()
        );
    }

    public static CommunityPostTradeListResponse masked(CommunityPost post, boolean isScrapped) {
        return new CommunityPostTradeListResponse(
                post.getId(),
                "차단한 사용자의 게시물입니다.",
                post.getCategory().getDescription(),
                post.getTradeCategory().getDescription(),
                post.getTradeMethod().getDescription(),
                isScrapped,
                post.getImages().isEmpty() ? "basic" : post.getImages().get(0).getImage().getImageUrl()
        );
    }
}
