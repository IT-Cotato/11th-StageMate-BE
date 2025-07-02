package com.example.stagemate.domain.performances;

import com.example.stagemate.dto.data.CrawledPerformanceInfo;
import com.example.stagemate.global.converter.PerformanceStatusConverter;
import com.example.stagemate.global.converter.PerformanceTypeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Performances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId; // 공연 ID

    //인터파크에서 사용하는 item Id
    private String interparkPerformanceId;

    private String performanceName; // 공연 이름

    private String url; // 공연 상세 URL

    private String imageUrl; // 이미지 URL

    private String startDate; // 시작 날짜

    private String endDate; // 종료 날짜

    private String theaterName; // 극장 이름

    private String region; // 지역

    @Convert(converter = PerformanceTypeConverter.class)
    private PerformanceType performanceType; // 공연 유형 (뮤지컬, 연극 등)

    @Convert(converter = PerformanceStatusConverter.class)
    private PerformanceStatus performanceStatus; // 공연 상태 (예정, 진행중, 종료)

    //현재시간 기준으로 상태업데이트
    public void updateStatusBasedOnCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        //startDate는 20250701 형식
        LocalDate convertedStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate convertedendDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (convertedStartDate.isBefore(currentDate) && convertedendDate.isAfter(currentDate)) {
            this.performanceStatus = PerformanceStatus.ONGOING;
        } else if (convertedendDate.isBefore(currentDate)) {
            this.performanceStatus = PerformanceStatus.ENDED;
        } else {
            this.performanceStatus = PerformanceStatus.UPCOMING;
        }
    }

    public void updateFromCrawledData(Performances crawled) {
        this.performanceName = crawled.getPerformanceName();
        this.url = crawled.getUrl();
        this.imageUrl = crawled.getImageUrl();
        this.startDate = crawled.getStartDate();
        this.endDate = crawled.getEndDate();
        this.theaterName = crawled.getTheaterName();
        this.region = crawled.getRegion();
        this.performanceType = crawled.getPerformanceType();
        this.performanceStatus = crawled.getPerformanceStatus();
    }



    //crawledPerformanceInfo -> Performances
    public static Performances from(CrawledPerformanceInfo crawledPerformanceInfo) {
        return Performances.builder()
                .interparkPerformanceId(crawledPerformanceInfo.getInterparkPerformanceId())
                .performanceName(crawledPerformanceInfo.getPerformanceName())
                .url(crawledPerformanceInfo.getPerformanceUrl())
                .imageUrl(crawledPerformanceInfo.getImageUrl())
                .startDate(crawledPerformanceInfo.getStartDate())
                .endDate(crawledPerformanceInfo.getEndDate())
                .theaterName(crawledPerformanceInfo.getTheaterName())
                .region(crawledPerformanceInfo.getRegion())
                .performanceType(crawledPerformanceInfo.getPerformanceType())
                .performanceStatus(crawledPerformanceInfo.getPerformanceStatus())
                .build();
    }

}
