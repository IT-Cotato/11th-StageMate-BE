package com.example.stagemate.dto.response;

import com.example.stagemate.domain.notification.Notification;
import com.example.stagemate.global.util.DateFormat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long postId;
    private String title; // 댓글 내용
    private String content; // 알림 유형
    private String formattedDate;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .postId(notification.getTargetId())
                .title(notification.getContent())
                .content(notification.getType().getMessage())
                .formattedDate(DateFormat.formatDateTime(notification.getCreatedAt()))
                .build();
    }

}
