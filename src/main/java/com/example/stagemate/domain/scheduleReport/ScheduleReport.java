package com.example.stagemate.domain.scheduleReport;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.dto.request.ScheduleReportCreateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "schedule_reports")
public class ScheduleReport {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "schedule_report_id")
    private Long id;


    private String content;

    private LocalDateTime reportDate;

    private String url;

    @Enumerated(EnumType.STRING)
    private ScheduleReportStatus scheduleReportStatus;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    private Long userId;

    public void initReportStatus() {
        this.scheduleReportStatus = ScheduleReportStatus.PENDING;
    }

    public void changeReportStatus(ScheduleReportStatus scheduleReportStatus) {
        this.scheduleReportStatus = scheduleReportStatus;
    }

    public static ScheduleReport createWithInitialStatus(
            Performance performance, ScheduleReportCreateRequest request, Long userId) {
        ScheduleReport report = ScheduleReport.builder()
                .performance(performance)
                .content(request.getContent())
                .reportDate(LocalDateTime.now())
                .url(request.getUrl())
                .userId(userId)
                .build();

        report.initReportStatus();
        return report;
    }


}
