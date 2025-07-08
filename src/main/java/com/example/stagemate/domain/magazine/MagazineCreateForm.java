package com.example.stagemate.domain.magazine;

import com.example.stagemate.dto.request.MagazineCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// swagger 문서화를 위한 요청 DTO (문서화 전용)
@Schema(name = "MagazineCreateForm", description = "매거진 생성 폼")
@Getter
public class MagazineCreateForm {

    private String title;
    private String subTitle;
    private String content;
    private String category;

    @Schema(description = "이미지 파일들", type = "array", format = "binary")
    private List<MultipartFile> images = new ArrayList<>();
}
