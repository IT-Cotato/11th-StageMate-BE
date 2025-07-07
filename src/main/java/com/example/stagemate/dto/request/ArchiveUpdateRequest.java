package com.example.stagemate.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ArchiveUpdateRequest {
    private LocalDate viewingDate;
    private String casting;
    private String review;
    private double rating;
    private String memo;
    private String imageUrl;
}
