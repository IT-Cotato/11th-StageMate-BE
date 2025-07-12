package com.example.stagemate.domain.performance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "performance_scraps")
public class PerformanceScrap {

    @Id
    @jakarta.persistence.Column(name = "performance_scrap_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceScrapId;

    private Long userId; //user 객체 매핑 필요

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    private LocalDateTime scrapDate;

}
