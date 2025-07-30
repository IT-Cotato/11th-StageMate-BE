package com.example.stagemate.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class ArchiveUpdateRequest {
    private String title;

    @NotNull(message = "viewingDate cannot be null")
    private LocalDate viewingDate;

    @NotNull(message = "casting cannot be null")
    private String casting;

    @NotNull(message = "review cannot be null")
    private String review;

    @NotNull(message = "theaterName cannot be null")
    private String theaterName;

    
    //rating이 0~5 범위에서 0.5 간격으로 삽입 -> 어노테이션으로 0.5 간격까지 검증은 불가능해보임
    //추후에 validator로 리팩토링해볼 수 있을듯함
    @NotNull
    @jakarta.validation.constraints.Min(0)
    @jakarta.validation.constraints.Max(5)
    private Double rating;

    @NotNull(message = "memo cannot be null")
    private String memo;

    @NotNull(message = "imageUrl cannot be null")
    private String imageUrl;

    public void validate() {
        //모두 not null
        if (viewingDate == null || casting == null || review == null || theaterName == null || rating == null || memo == null) {
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
