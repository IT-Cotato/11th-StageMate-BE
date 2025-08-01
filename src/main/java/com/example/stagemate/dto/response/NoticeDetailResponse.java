package com.example.stagemate.dto.response;

import com.example.stagemate.domain.mypage.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private int viewCount;

    public static NoticeDetailResponse from(Notice notice) {
        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor().getNickname())
                .createdAt(notice.getCreatedAt())
                .viewCount(notice.getViewCount())
                .build();
    }
}
