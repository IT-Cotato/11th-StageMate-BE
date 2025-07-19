package com.example.stagemate.domain.performanceSchedule;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "performance_schedule_report_category")
public class PerformanceScheduleReportCategory {
    @jakarta.persistence.Id
    @jakarta.persistence.Column(name = "schedule_report_category_id")
    private Long id;

    @ManyToOne
    @jakarta.persistence.JoinColumn(name = "schedule_report_id")
    private PerformanceSchedule performanceSchedule;

    private PerformanceScheduleReportCategoryType performanceScheduleReportCategoryType;

    private int categoryOrder;

}
