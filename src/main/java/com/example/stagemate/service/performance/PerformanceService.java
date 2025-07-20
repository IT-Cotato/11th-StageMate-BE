package com.example.stagemate.service.performance;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceScrap;
import com.example.stagemate.domain.performance.PerformanceStatistics;
import com.example.stagemate.domain.performance.PerformanceStatus;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.PerformanceDetailResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final PerformanceScrapRepository performanceScrapRepository;
    private final PerformanceStatisticsRepository performanceStatisticsRepository;

    //공연 상세 정보 가져오기
    public PerformanceDetailResponse getPerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new AppException(PerformanceErrorCode.NOT_FOUND));

        return PerformanceDetailResponse.from(performance);
    }

    public List<PerformanceDetailResponse> findOngoingOrUpcomingPerformances() {
        // 상영중인 공연 가져오기
        List<Performance> performances = performanceRepository.findByPerformanceStatusIn(List.of(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING));

        return performances.stream()
                .map(PerformanceDetailResponse::from)
                .collect(Collectors.toList());
    }

    public void insertOrDeletePerformanceScrap(UserJpaEntity user, Long performanceId) {
        boolean isExist = performanceScrapRepository.
                existsByPerformanceIdAndUserId(performanceId, user.getId());

        //기존에 존재하면 삭제
        if (isExist) {
            performanceScrapRepository.deleteByPerformanceIdAndUserId(performanceId, user.getId());
            return;
        }

        //기존에 존재하지 않음면 저장
        PerformanceScrap performanceScrap = PerformanceScrap.builder().
                performance(performanceRepository.findById(performanceId).orElseThrow(() -> new AppException(PerformanceErrorCode.NOT_FOUND)))
                .user(user)
                .scrapDate(LocalDateTime.now())
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
