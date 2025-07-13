package com.example.stagemate.service.performance;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceScrap;
import com.example.stagemate.domain.performance.PerformanceStatistics;
import com.example.stagemate.domain.performance.PerformanceStatus;
import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.dto.response.RecommendedPerformanceResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.performances.PerformanceErrorCode;
import com.example.stagemate.repository.PerformanceRepository;
import com.example.stagemate.repository.PerformanceScheduleRepository;
import com.example.stagemate.repository.PerformanceScrapRepository;
import com.example.stagemate.repository.PerformanceStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final PerformanceScrapRepository performanceScrapRepository;
    private final PerformanceStatisticsRepository performanceStatisticsRepository;

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

    public void createPerformanceScrap(Long performanceId) {
        PerformanceScrap performanceScrap = PerformanceScrap.builder().
                performance(performanceRepository.findById(performanceId).orElseThrow(() -> new AppException(PerformanceErrorCode.NOT_FOUND)))
                .userId(1L) //유저 객체 매핑 필요
                .build();

        performanceScrapRepository.save(performanceScrap);
    }

    //추천 공연은 1시간이내 스크랩이 많이 오른 순으로 추천
    public List<RecommendedPerformanceResponse> getRecommendPerformances(Pageable pageable) {
        //increased_scrap_count 높은 순 공연통계 찾기
        List<PerformanceStatistics> performanceStatistics = performanceStatisticsRepository.findTopByIncreasedScrapCount(pageable);


        return performanceStatistics.stream()
                .map(stats -> RecommendedPerformanceResponse.from(stats.getPerformance(), stats))
                .collect(Collectors.toList());
    }


}
