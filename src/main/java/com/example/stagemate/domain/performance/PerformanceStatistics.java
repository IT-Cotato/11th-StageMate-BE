package com.example.stagemate.domain.performance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "performance_statistics")
public class PerformanceStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_statistics_id")
    private Long id;

    @JoinColumn(name = "performance_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Performance performance;

    private Long increasedScrapCount;

}
