package com.example.stagemate.dto.request;

import com.example.stagemate.domain.magazine.Magazine;
import com.example.stagemate.domain.magazine.MagazineCategory;
import com.example.stagemate.domain.magazine.MagazineCreateForm;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MagazineCreateRequest {
    private String title;
    private String subTitle;
    private String content;
    private String category;

    public static MagazineCreateRequest from(MagazineCreateForm form) {
        MagazineCreateRequest request = new MagazineCreateRequest();
        request.title = form.getTitle();
        request.subTitle = form.getSubTitle();
        request.content = form.getContent();
        request.category = form.getCategory();
        return request;
    }

    public Magazine toEntity(MagazineCategory magazineCategory) {
        return Magazine.builder()
                .title(title)
                .subTitle(subTitle)
                .content(content)
                .category(magazineCategory)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
