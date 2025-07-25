package com.example.stagemate.repository;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.performance.PerformanceStatus;
import com.example.stagemate.domain.performance.PerformanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByPerformanceStatus(PerformanceStatus performanceStatus);

    //fetch join, Theater 정보까지, 파라미터는 statues
    @Query("SELECT p FROM Performance p JOIN FETCH p.theater WHERE p.performanceStatus IN :statuses")
    List<Performance> findByPerformanceStatusIn(List<PerformanceStatus> statuses);

    @Query("""
    SELECT p FROM Performance p
    JOIN p.theater t
    WHERE (:type IS NULL OR p.performanceType = :type)
      AND (:genre IS NULL OR p.performanceGenre = :genre)
      AND (:date IS NULL OR (p.startDate <= :date AND p.endDate >= :date))
      AND (:regions IS NULL OR t.region IN :regions)
    ORDER BY p.id
""")
    Page<Performance> findByCondition(
            @Param("type") PerformanceType performanceType,
            @Param("genre") PerformanceGenre performanceGenre,
            @Param("regions") List<String> regions,
            @Param("date") LocalDate date,
            Pageable pageable
    );



}
