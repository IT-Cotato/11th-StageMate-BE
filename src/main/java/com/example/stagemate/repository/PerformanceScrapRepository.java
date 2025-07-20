package com.example.stagemate.repository;

import com.example.stagemate.domain.performance.PerformanceScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceScrapRepository extends JpaRepository<PerformanceScrap, Long> {
    void deleteByPerformanceIdAndUserId(Long performanceId, Long userId);
    boolean existsByPerformanceIdAndUserId(Long performanceId, Long userId);
}
