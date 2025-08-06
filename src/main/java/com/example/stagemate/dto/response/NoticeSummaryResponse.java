package com.example.stagemate.dto.response;

import com.example.stagemate.domain.mypage.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeSummaryResponse {
    private Long id;
    private String title;
    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dd")
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
