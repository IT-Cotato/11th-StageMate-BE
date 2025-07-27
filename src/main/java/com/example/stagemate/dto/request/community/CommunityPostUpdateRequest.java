package com.example.stagemate.dto.request.community;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.util.List;

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
}
