package com.example.stagemate.dto.response;

import com.example.stagemate.domain.notification.Notification;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long postId;
    private String title; // 댓글
    private String content;
    private String createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .postId(notification.getTargetId())
                .title(notification.getContent())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }

}
