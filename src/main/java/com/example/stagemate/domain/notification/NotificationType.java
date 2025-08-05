package com.example.stagemate.domain.notification;

import lombok.Getter;

@Getter
public enum NotificationType {
    COMMENT_ON_POST("누군가 댓글을 남겼어요"),
    REPLY_ON_COMMENT("댓글에 누군가 답장을 했어요");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

}
