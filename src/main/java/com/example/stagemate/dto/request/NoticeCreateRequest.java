package com.example.stagemate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCreateRequest {

    @Schema(description = "공지사항 제목", example = "서비스 점검 안내")
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(description = "공지사항 본문", example = "서비스 점검이 2023년 10월 1일에 진행됩니다.")
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
