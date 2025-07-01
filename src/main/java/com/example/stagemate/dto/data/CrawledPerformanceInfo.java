package com.example.stagemate.dto.data;

import com.example.stagemate.global.code.gubun.PerformanceType;
import com.example.stagemate.global.code.status.PerformanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class CrawledPerformanceInfo {
    private String interparkPerformanceId;
    private String performanceName;
    private String performanceUrl;
    private String imageUrl;
    private String startDate;
    private String endDate;
    private String theaterName;
    private String region;
    private PerformanceType performanceType;
    private PerformanceStatus performanceStatus;
}
