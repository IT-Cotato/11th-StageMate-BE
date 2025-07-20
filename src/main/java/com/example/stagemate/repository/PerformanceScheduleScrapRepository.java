package com.example.stagemate.repository;

import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceScheduleScrapRepository extends JpaRepository<PerformanceScheduleScrap, Long> {
    List<PerformanceScheduleScrap> findByUserId(Long userId);

    //유저 스크랩 여부
    boolean existsByPerformanceScheduleIdAndUserId(Long performanceScheduleId, Long userId);

    void deleteByPerformanceScheduleIdAndUserId(Long performanceScheduleId, Long userId);
}
