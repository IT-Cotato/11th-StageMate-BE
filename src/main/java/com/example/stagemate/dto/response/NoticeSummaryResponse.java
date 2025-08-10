package com.example.stagemate.dto.response;

import com.example.stagemate.domain.mypage.Notice;
import com.example.stagemate.global.util.DateFormat;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NoticeSummaryResponse {
    private Long id;
    private String title;
    private String author;
    String createdAt;
    private int viewCount;

    public static NoticeSummaryResponse from(Notice notice) {
        return NoticeSummaryResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .author(notice.getAuthor().getNickname())
                .createdAt(DateFormat.formatDateOnly(notice.getCreatedAt()))
                .viewCount(notice.getViewCount())
                .build();
    }
}
