package com.example.stagemate.dto.request.community;

import com.example.stagemate.domain.community.CommunityCategory;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.community.TradeCategory;
import com.example.stagemate.domain.community.TradeMethod;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.global.exception.AppException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_POST_CREATE_INVALID_INPUT;

@Getter
public class CommunityPostCreateRequest {
    private String title;
    private JsonNode content;
    private String category; // 일상, 나눔거래, 꿀팁
    private String tradeCategory; // 뮤지컬, 연극
    private String tradeMethod; // 추첨나눔, 판매, 선착나눔
    private boolean membersOnly;
    private boolean sendNotification;

    public CommunityPost toEntity(UserJpaEntity user, CommunityCategory communityCategory, TradeCategory tradeCategory, TradeMethod tradeMethod, String jsonString) {
        return CommunityPost.builder()
                .title(title)
                .category(communityCategory)
                .tradeCategory(tradeCategory) // 나눔거래가 아닐 경우 null
                .tradeMethod(tradeMethod) // 나눔거래가 아닐 경우 null
                .content(jsonString)
                .author(user)
                .membersOnly(membersOnly)
                .sendNotification(sendNotification)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }

    public void validate() {
        if (isBlank(title) || content == null || isBlank(category)
                || ("나눔거래".equals(category) && (isBlank(tradeCategory) || isBlank(tradeMethod)))) {
            throw new AppException(COMMUNITY_POST_CREATE_INVALID_INPUT);
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
