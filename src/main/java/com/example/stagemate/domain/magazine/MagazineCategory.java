package com.example.stagemate.domain.magazine;

import com.example.stagemate.global.exception.magazine.MagazineCategoryNotFoundException;
import lombok.Getter;

import static com.example.stagemate.global.exception.magazine.MagazineErrorCode.CATEGORY_NOT_FOUND;

@Getter
public enum MagazineCategory {
    MUSICAL("뮤지컬"),
    PLAY("연극");

    private String description;

    MagazineCategory(String description) {
        this.description = description;
    }

    // request의 카테고리를 MagazineCategory enum으로 변환(한국어 -> 영어)
    public static MagazineCategory from(String category) {
        System.out.println(category);
        for (MagazineCategory magazineCategory : MagazineCategory.values()) {
            if (magazineCategory.getDescription().equals(category)) {
                System.out.println("매거진 카테고리: " + magazineCategory);
                return magazineCategory;
            }
        }
        throw new MagazineCategoryNotFoundException(CATEGORY_NOT_FOUND);
    }
}
