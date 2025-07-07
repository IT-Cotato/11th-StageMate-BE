package com.example.stagemate.repository;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByPerformanceStatus(PerformanceStatus performanceStatus);
    List<Performance> findByPerformanceStatusIn(List<PerformanceStatus> statuses);
    List<Performance> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
