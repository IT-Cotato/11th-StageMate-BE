package com.example.stagemate.service.scheduleReport;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.scheduleReport.ScheduleReport;
import com.example.stagemate.domain.scheduleReport.ScheduleReportErrorCode;
import com.example.stagemate.domain.scheduleReport.ScheduleReportStatus;
import com.example.stagemate.dto.request.ScheduleReportCreateRequest;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.ScheduleReportRepository;
import com.example.stagemate.service.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleReportService {
    private final ScheduleReportRepository scheduleReportRepository;
    private final PerformanceService performanceService;

    public Long createScheduleReport(ScheduleReportCreateRequest scheduleReportCreateRequest) {
        Performance performance = performanceService.getPerformance(scheduleReportCreateRequest.getPerformanceId());

        ScheduleReport scheduleReport =
                ScheduleReport.createWithInitialStatus(performance, scheduleReportCreateRequest, 1L);

        return scheduleReportRepository.save(scheduleReport).getId();
    }

    //스케줄 리포트 상세 정보 가져오기
    public ScheduleReport getScheduleReport(Long scheduleReportId) {
        return scheduleReportRepository.findById(scheduleReportId)
                .orElseThrow(() -> new AppException(ScheduleReportErrorCode.NOT_FOUND));
    }

    public List<ScheduleReport> getScheduleReports(ScheduleReportStatus scheduleReportStatus) {
        return scheduleReportRepository.findByScheduleReportStatus(scheduleReportStatus);
    }

    public ScheduleReport changeScheduleReportStatus(Long scheduleReportId, ScheduleReportStatus scheduleReportStatus) {
        ScheduleReport scheduleReport = getScheduleReport(scheduleReportId);
        scheduleReport.changeReportStatus(scheduleReportStatus);
        return scheduleReportRepository.save(scheduleReport);
    }
}
