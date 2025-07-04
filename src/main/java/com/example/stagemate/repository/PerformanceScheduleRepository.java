package com.example.stagemate.repository;

import com.example.stagemate.domain.performanceSchedules.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedules.PerformanceScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
    List<PerformanceSchedule> findByPerformance_PerformanceIdIn(List<Long> performanceIds);
    PerformanceSchedule findByPerformance_PerformanceIdAndPerformanceScheduleType(Long performanceId, PerformanceScheduleType type);
    List<PerformanceSchedule> findByScheduleDate(LocalDate scheduleDate);

    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.performance WHERE ps.scheduleDate BETWEEN :startDate AND :endDate")
    List<PerformanceSchedule> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
}
