package com.example.stagemate.dto.request.community;

import com.example.stagemate.global.exception.AppException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.util.List;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_POST_CREATE_INVALID_INPUT;

@Getter
public class CommunityPostUpdateRequest {
    private String title;
    private JsonNode content;
    private String category; // 일상, 나눔거래, 꿀팁
    private String tradeCategory; // 뮤지컬, 연극
    private String tradeMethod; // 추첨나눔, 판매, 선착나눔
    private boolean membersOnly;
    private List<Long> imageIds; // 삭제하지 않은 기존 이미지 ID 목록
    private boolean sendNotification;

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
