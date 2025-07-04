package com.example.stagemate.dto.data;

import com.example.stagemate.domain.performances.PerformanceGenre;
import com.example.stagemate.domain.performances.PerformanceType;
import com.example.stagemate.domain.performances.PerformanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@Builder
public class CrawledPerformanceInfo {
    private String interparkPerformanceId;
    private String performanceName;
    private String performanceUrl;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String theaterName;
    private String region;
    private PerformanceGenre performanceGenre;

    private PerformanceType performanceType;
    private PerformanceStatus performanceStatus;
}
