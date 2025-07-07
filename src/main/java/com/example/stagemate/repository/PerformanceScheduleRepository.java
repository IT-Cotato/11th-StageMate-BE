package com.example.stagemate.repository;

import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
    PerformanceSchedule findByPerformance_IdAndPerformanceScheduleType(Long performanceId, PerformanceScheduleType type);
    List<PerformanceSchedule> findByScheduleDate(LocalDate scheduleDate);

    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.performance WHERE ps.scheduleDate BETWEEN :startDate AND :endDate")
    List<PerformanceSchedule> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
}
