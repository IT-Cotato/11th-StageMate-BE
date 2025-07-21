package com.example.stagemate.domain.performance;

import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.dto.data.CrawledPerformanceInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "performances")
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id; // 공연 ID

    //인터파크에서 사용하는 item Id
    private String interparkPerformanceId;

    private String performanceName; // 공연 이름

    private String url; // 공연 상세 URL

    private String imageUrl; // 이미지 URL

    private LocalDate startDate; // 시작 날짜

    private LocalDate endDate; // 종료 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater; // 극장 이름

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PerformanceScrap> scraps = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PerformanceGenre performanceGenre;

    @Enumerated(EnumType.STRING)
    private PerformanceType performanceType; // 공연 유형 (뮤지컬, 연극 등)

    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus; // 공연 상태 (예정, 진행중, 종료)

    //현재시간 기준으로 상태업데이트
    public void updateStatusBasedOnCurrentDate() {
        LocalDate currentDate = LocalDate.now();

        if (startDate.isBefore(currentDate) && endDate.isAfter(currentDate)) {
            this.performanceStatus = PerformanceStatus.ONGOING;
        } else if (endDate.isBefore(currentDate)) {
            this.performanceStatus = PerformanceStatus.ENDED;
        } else {
            this.performanceStatus = PerformanceStatus.UPCOMING;
        }
    }

    public void updateFromCrawledData(Performance crawled) {
        this.interparkPerformanceId = crawled.getInterparkPerformanceId();
        this.performanceName = crawled.getPerformanceName();
        this.url = crawled.getUrl();
        this.imageUrl = crawled.getImageUrl();
        this.startDate = crawled.getStartDate();
        this.endDate = crawled.getEndDate();
        this.theater = crawled.getTheater();
        this.performanceType = crawled.getPerformanceType();
        this.performanceStatus = crawled.getPerformanceStatus();
        this.performanceGenre = crawled.getPerformanceGenre();
    }



    //crawledPerformanceInfo -> Performance
    public static Performance from(CrawledPerformanceInfo crawledPerformanceInfo, Theater theater) {
        return Performance.builder()
                .interparkPerformanceId(crawledPerformanceInfo.getInterparkPerformanceId())
                .performanceName(crawledPerformanceInfo.getPerformanceName())
                .url(crawledPerformanceInfo.getPerformanceUrl())
                .imageUrl(crawledPerformanceInfo.getImageUrl())
                .startDate(crawledPerformanceInfo.getStartDate())
                .endDate(crawledPerformanceInfo.getEndDate())
                .theater(theater)
                .performanceType(crawledPerformanceInfo.getPerformanceType())
                .performanceStatus(crawledPerformanceInfo.getPerformanceStatus())
                .performanceGenre(crawledPerformanceInfo.getPerformanceGenre())
                .build();
    }

}
