package com.example.stagemate.repository;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByPerformanceStatus(PerformanceStatus performanceStatus);

    //fetch join, Theater 정보까지, 파라미터는 statues
    @Query("SELECT p FROM Performance p JOIN FETCH p.theater WHERE p.performanceStatus IN :statuses")
    List<Performance> findByPerformanceStatusIn(List<PerformanceStatus> statuses);
}
