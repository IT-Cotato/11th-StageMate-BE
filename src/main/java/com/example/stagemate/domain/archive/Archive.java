package com.example.stagemate.domain.archive;

import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "archives")
public class Archive {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "archive_id")
    private Long id;

    private LocalDate viewingDate;

    private String casting;

    private String review;

    private double rating;

    private String memo;

    private String imageUrl;

    private String theaterName;

    private String userId; // User 매핑 필요



    public static Archive create(ArchiveCreateRequest request) {
        return Archive.builder()
                .viewingDate(request.getViewingDate())
                .casting(request.getCasting())
                .review(request.getReview())
                .rating(request.getRating())
                .memo(request.getMemo())
                .imageUrl(request.getImageUrl())
                .theaterName(request.getTheaterName())
                .userId(request.getUserId())
                .build();
    }

    public void update(ArchiveUpdateRequest request) {
        this.viewingDate = request.getViewingDate();
        this.casting = request.getCasting();
        this.review = request.getReview();
        this.rating = request.getRating();
        this.memo = request.getMemo();
        this.imageUrl = request.getImageUrl();
        this.theaterName = request.getTheaterName();
    }
}
