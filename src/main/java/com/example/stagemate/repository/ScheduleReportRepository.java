package com.example.stagemate.repository;

import com.example.stagemate.domain.scheduleReport.ScheduleReport;
import com.example.stagemate.domain.scheduleReport.ScheduleReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleReportRepository extends JpaRepository<ScheduleReport, Long> {
    List<ScheduleReport> findByScheduleReportStatus(ScheduleReportStatus scheduleReportStatus);
}
