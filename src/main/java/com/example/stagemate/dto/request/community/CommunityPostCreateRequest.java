package com.example.stagemate.dto.request.community;

import com.example.stagemate.domain.community.CommunityCategory;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.community.TradeCategory;
import com.example.stagemate.domain.community.TradeMethod;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunityPostCreateRequest {
    private String title;
    private JsonNode content;
    private String category; // 일상, 나눔거래, 꿀팁
    private String tradeCategory; // 뮤지컬, 연극
    private String tradeMethod; // 추첨나눔, 판매, 선착나눔
    private boolean membersOnly;

    public CommunityPost toEntity(UserJpaEntity user, CommunityCategory communityCategory, TradeCategory tradeCategory, TradeMethod tradeMethod, String jsonString) {
        return CommunityPost.builder()
                .title(title)
                .category(communityCategory)
                .tradeCategory(tradeCategory) // 나눔거래가 아닐 경우 null
                .tradeMethod(tradeMethod) // 나눔거래가 아닐 경우 null
                .content(jsonString)
                .author(user)
                .membersOnly(membersOnly)
//                .isDeleted(false)
//                .viewCount(0)
//                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
