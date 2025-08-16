package com.example.stagemate.repository.performance;

import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerformanceScheduleScrapRepository extends JpaRepository<PerformanceScheduleScrap, Long> {
    List<PerformanceScheduleScrap> findByUserId(Long userId);

    //유저 스크랩 여부
    boolean existsByPerformanceScheduleIdAndUserId(Long performanceScheduleId, Long userId);

    void deleteByPerformanceScheduleIdAndUserId(Long performanceScheduleId, Long userId);

    @Query("""
        select s.performanceSchedule.id
        from PerformanceScheduleScrap s
        where s.user.id = :userId and s.performanceSchedule.id in :scheduleIds
    """)
    List<Long> findScrapedScheduleIdsByUser(Long userId, List<Long> scheduleIds);
}
