package com.example.stagemate.repository;

import com.example.stagemate.domain.performances.Performances;
import com.example.stagemate.domain.performances.PerformanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performances, String> {
    List<Performances> findByPerformanceStatus(PerformanceStatus performanceStatus);
    List<Performances> findByPerformanceStatusIn(List<PerformanceStatus> statuses);
}
