package com.example.stagemate.dto.response;

import com.example.stagemate.domain.mypage.Notice;
import com.example.stagemate.global.util.DateFormat;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NoticeDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    String createdAt;
    private int viewCount;

    public static NoticeDetailResponse from(Notice notice) {
        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor().getNickname())
                .createdAt(DateFormat.formatTimeIfTodayElseDate(notice.getCreatedAt()))
                .viewCount(notice.getViewCount())
                .build();
    }
}
