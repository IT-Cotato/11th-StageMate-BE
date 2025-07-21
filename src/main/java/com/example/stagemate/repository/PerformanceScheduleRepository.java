package com.example.stagemate.repository;

import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleType;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
    PerformanceSchedule findByPerformance_IdAndPerformanceScheduleType(Long performanceId, PerformanceScheduleType type);
    List<PerformanceSchedule> findByScheduleDateAndPerformanceScheduleReportStatus(LocalDate scheduleDate, PerformanceScheduleReportStatus status);

    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.performance " +
            "WHERE ps.scheduleDate BETWEEN :startDate AND :endDate AND ps.performanceScheduleReportStatus = :status")
    List<PerformanceSchedule> findByScheduleDateBetween(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate, @Param("status") PerformanceScheduleReportStatus status);


    //공연 상태별 조회
    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.performance WHERE ps.performanceScheduleReportStatus IN :status")
    List<PerformanceSchedule> findByScheduleReportStatus(@Param("status") List<PerformanceScheduleReportStatus> status);

    //findByIdWithPerformanceScheduleReportCategories
    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.performance JOIN FETCH ps.performanceScheduleReportCategories WHERE ps.id = :performanceScheduleId")
    Optional<PerformanceSchedule> findByIdWithPerformanceScheduleReportCategories(@Param("performanceScheduleId") Long performanceScheduleId);

}
