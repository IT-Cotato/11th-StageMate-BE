package com.example.stagemate.dto.request.community;

import lombok.Getter;

@Getter
public class UserBlockRequest {
    private Long targetId;         // 게시글 ID or 댓글 ID
    private String targetType;     // POST 또는 COMMENT
}
