package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.global.util.DateFormat;

import java.util.List;
import java.util.Set;

public record CommunityCommentResponse(
        Long id,
        String writer,
        String time, // 오래된 경우 댓글 작성 날짜, 오늘인 경우 (몇 시간 전 / 몇 분 전)
        String content,
        boolean isEdited, // 수정 여부
        String profileImage, // 프로필 이미지 URL
        List<CommunityCommentResponse> children
) {
    public CommunityCommentResponse(CommunityComment comment, Set<Long> blockedUserIds) {
        this(
                comment.getId(),
                comment.getUser().getNickname(),  // user 닉네임 or 이름
                DateFormat.formatTimeIfTodayElseDateTime(comment.getCreatedAt()),
                resolveContent(comment, blockedUserIds),
                !comment.getCreatedAt().equals(comment.getUpdatedAt()),
                comment.getUser().getProfileImageUrl(),
                comment.getChildren().stream()
                        .map(child -> new CommunityCommentResponse(child, blockedUserIds))
                        .toList()
        );
    }


    private static String resolveContent(CommunityComment comment, Set<Long> blockedUserIds) {
        // 댓글을 보여줄 때 차단을 삭제된 것보다 우선순위로 처리
        if (blockedUserIds.contains(comment.getUser().getId())) {
            return "[차단한 사용자의 댓글입니다]";
        }
        if (comment.isDeleted()) {
            return "[삭제된 댓글입니다]";
        }
        return comment.getContent();
    }
}

