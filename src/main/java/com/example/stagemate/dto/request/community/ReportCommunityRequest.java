package com.example.stagemate.dto.request.community;

import lombok.Getter;

@Getter
public class ReportCommunityRequest {
    private Long targetId;         // 게시글 or 댓글 ID
    private String targetType;     // POST, COMMENT
    private String reason;         // 신고 사유
}
