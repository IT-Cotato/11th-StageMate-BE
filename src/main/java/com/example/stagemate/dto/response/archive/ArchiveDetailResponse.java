package com.example.stagemate.dto.response.archive;

import com.example.stagemate.domain.archive.Archive;
import lombok.Builder;

import java.time.LocalDate;
@Builder
public record ArchiveDetailResponse(
        LocalDate viewingDate,
        String casting,
        String review,
        double rating,
        String memo,
        String imageUrl,
        String theaterName,
        String title,
        Long id
) {
    public static ArchiveDetailResponse from(Archive archive) {
        return ArchiveDetailResponse.
                builder()
                .viewingDate(archive.getViewingDate())
                .casting(archive.getCasting())
                .review(archive.getReview())
                .rating(archive.getRating())
                .memo(archive.getMemo())
                .imageUrl(archive.getImage().getImageUrl())
                .theaterName(archive.getTheaterName())
                .title(archive.getTitle())
                .id(archive.getId())
                .build();
    }
}


//    private Long id;
//
//    private LocalDate viewingDate;
//
//    private String casting;
//
//    private String review;
//
//    private double rating;
//
//    private String memo;
//
//    private String imageUrl;
//
//    private String theaterName;
//
//    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
//    @jakarta.persistence.JoinColumn(name = "user_id")
//    private UserJpaEntity user;
//


