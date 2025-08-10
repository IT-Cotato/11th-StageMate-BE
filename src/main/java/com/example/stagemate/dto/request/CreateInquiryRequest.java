package com.example.stagemate.dto.request;

import com.example.stagemate.domain.mypage.InquiryCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "1:1 문의 작성 요청 DTO")
public record CreateInquiryRequest(

        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 3, max = 100, message = "제목은 3자 이상 100자 이하로 입력해주세요.")
        String title,

        @NotNull(message = "카테고리를 선택해주세요.")
        InquiryCategory category,

        @Schema(description = "문의 내용", example = "어제부터 로그인이 되지 않고 401 에러가 발생합니다.")
        @NotBlank(message = "문의 내용은 필수 입력값입니다.")
        @Size(min = 3, max = 5000, message = "문의 내용은 3자 이상 5000자 이하로 입력해주세요.")
        String content
) {}