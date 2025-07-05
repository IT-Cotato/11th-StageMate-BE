package com.example.stagemate.service;

import com.example.stagemate.domain.performanceSchedules.PerformanceSchedule;
import com.example.stagemate.domain.performances.Performance;
import com.example.stagemate.domain.performances.PerformanceStatus;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.performances.PerformanceErrorCode;
import com.example.stagemate.repository.PerformanceRepository;
import com.example.stagemate.repository.PerformanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;

    //공연 상세 정보 가져오기
    public Performance getPerformance(Long performanceId) {
        return performanceRepository.findById(performanceId)
                .orElseThrow(() -> new AppException(PerformanceErrorCode.NOT_FOUND));
    }

    //공연 스케줄 목록
    public List<PerformanceSchedule> getPerformanceSchedule(Integer year, Integer month) {
        //year, month -> LocalDate
        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return performanceScheduleRepository.findByScheduleDateBetween(startDate,endDate);
    }

    //공연 스케줄 목록
    public List<PerformanceSchedule> getPerformanceSchedule(Integer year,Integer month,Integer day) {
        //year, month, day -> LocalDate
        LocalDate date = LocalDate.of(year,month,day);

        return performanceScheduleRepository.findByScheduleDate(date);
    }



    public List<Performance> findOngoingPerformances() {
        // 상영중인 공연 가져오기
        return performanceRepository.findByPerformanceStatus(PerformanceStatus.ONGOING);
    }
}
