package com.example.stagemate.dto.response;

import com.example.stagemate.domain.notification.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                .formattedDate(formatDate(notification.getCreatedAt()))
                .build();
    }

    private static String formatDate(LocalDateTime createdAt) {
        LocalDate createdDate = createdAt.toLocalDate();
        LocalDate today = LocalDate.now();

        if (createdDate.isEqual(today)) {
            return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        }
    }

}
