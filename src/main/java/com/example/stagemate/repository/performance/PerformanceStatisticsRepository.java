package com.example.stagemate.repository.performance;

import com.example.stagemate.domain.performance.PerformanceStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PerformanceStatisticsRepository extends JpaRepository<PerformanceStatistics, Long> {
    @Modifying
    @Query("DELETE FROM PerformanceStatistics ps")
    void deleteAllPerformanceStatistics();


    @Modifying
    @Query(value = """
    INSERT INTO performance_statistics (performance_id, increased_scrap_count)
    SELECT
        performance_id,
        COUNT(*) AS increased_scrap_count
    FROM performance_scraps
    WHERE scrap_date >= NOW() - INTERVAL 1 HOUR
    GROUP BY performance_id
    """, nativeQuery = true)
    void updateIncreasedScrapCountInBulk();


    @Query(
            value = "SELECT ps FROM PerformanceStatistics ps JOIN FETCH ps.performance ORDER BY ps.increasedScrapCount DESC",
            countQuery = "SELECT COUNT(ps) FROM PerformanceStatistics ps"
    )
    Page<PerformanceStatistics> findTopByIncreasedScrapCount(Pageable pageable);


}
