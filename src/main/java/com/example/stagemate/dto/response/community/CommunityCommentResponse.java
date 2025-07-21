package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityComment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record CommunityCommentResponse(
        Long id,
        String writer,
        String time, // 오래된 경우 댓글 작성 날짜, 오늘인 경우 (몇 시간 전 / 몇 분 전)
        String content,
        boolean isEdited, // 수정 여부
        List<CommunityCommentResponse> children
) {
    public CommunityCommentResponse(CommunityComment comment) {
        this(
                comment.getId(),
                comment.getUser().getNickname(),  // user 닉네임 or 이름
                formatTime(comment.getCreatedAt()),
                comment.isDeleted() ? "[삭제된 댓글입니다]" : comment.getContent(),
                !comment.getCreatedAt().equals(comment.getUpdatedAt()),
                comment.getChildren().stream()
                        .map(CommunityCommentResponse::new)
                        .toList()
        );
    }

    private static String formatTime(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        if (minutes < 1) return "방금 전";
        else if (minutes < 60) return minutes + "분 전";
        else if (hours < 24) return hours + "시간 전";
        else return createdAt.toLocalDate().toString(); // 2025-07-21
    }
}

