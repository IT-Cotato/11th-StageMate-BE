package com.example.stagemate.dto.response;

import com.example.stagemate.domain.mypage.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeSummaryResponse {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private int viewCount;

    public static NoticeSummaryResponse from(Notice notice) {
        return NoticeSummaryResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .author(notice.getAuthor().getNickname())
                .createdAt(notice.getCreatedAt())
                .viewCount(notice.getViewCount())
                .build();
    }
}
