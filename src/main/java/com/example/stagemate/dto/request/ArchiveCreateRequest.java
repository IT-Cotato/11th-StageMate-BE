package com.example.stagemate.dto.request;

import com.example.stagemate.domain.archive.Archive;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class ArchiveCreateRequest {
    private String title;

    private LocalDate viewingDate;

    private String casting;

    private String review;

    private String theaterName;

    private Double rating;

    private String memo;

    private String naverImageUrl;


    public void validate() {
        //모두 not null
        if (viewingDate == null || casting == null || review == null || theaterName == null || rating == null || memo == null || title == null) {
            throw new IllegalArgumentException("All fields must be not null.");
        }

        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 0 and 5.");
        }

        if (rating % 0.5 != 0) {
            throw new IllegalArgumentException("rating must be a multiple of 0.5.");
        }



    }

}
