package com.example.stagemate.dto.response;

import com.example.stagemate.domain.notification.Notification;
import com.example.stagemate.global.util.DateFormat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long postId;
    private String category; // 게시글 카테고리
    private String title; // 댓글 내용
    private String content; // 알림 유형
    private String formattedDate;

    public static NotificationResponse from(Notification notification, String postCategory) {
        return NotificationResponse.builder()
                .postId(notification.getTargetId())
                .category(postCategory)
                .title(notification.getContent())
                .content(notification.getType().getMessage())
                .formattedDate(DateFormat.formatTimeIfTodayElseDateTime(notification.getCreatedAt()))
                .build();
    }

}
