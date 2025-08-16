package com.example.stagemate.domain.archive;

import com.example.stagemate.domain.image.Image;
import com.example.stagemate.domain.user.UserErrorCode;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.global.exception.AppException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

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

    private String title;

    private LocalDate viewingDate;

    private String casting;

    private String review;

    private double rating;

    private String memo;

    private String theaterName;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "image_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Image image;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserJpaEntity user;



    public static Archive create(ArchiveCreateRequest request, UserJpaEntity user, Image image) {
        return Archive.builder()
                .title(request.getTitle())
                .viewingDate(request.getViewingDate())
                .casting(request.getCasting())
                .review(request.getReview())
                .rating(request.getRating())
                .memo(request.getMemo())
                .theaterName(request.getTheaterName())
                .image(image)
                .user(user)
                .build();
    }

    public void validateDeleteOrUpdateBy(UserJpaEntity user) {
        if (!this.user.getId().equals(user.getId())) {
            throw new AppException(UserErrorCode.NO_PERMISSION);
        }
    }

    //이미지는 변경 불가능
    public void update(ArchiveUpdateRequest request) {
        this.title = request.getTitle();
        this.viewingDate = request.getViewingDate();
        this.casting = request.getCasting();
        this.review = request.getReview();
        this.rating = request.getRating();
        this.memo = request.getMemo();
        this.theaterName = request.getTheaterName();
    }
}
