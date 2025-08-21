package com.example.stagemate.dto.request.community;

import lombok.Getter;

@Getter
public class UserBlockRequest {
    private Long blockedUserId;         // 차단대상 유저ID
}


