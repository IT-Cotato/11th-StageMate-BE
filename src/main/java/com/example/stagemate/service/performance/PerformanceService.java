package com.example.stagemate.service.performance;

import com.example.stagemate.domain.event.Event;
import com.example.stagemate.domain.performance.*;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.event.EventResponse;
import com.example.stagemate.dto.response.performance.PerformanceDetailResponse;
import com.example.stagemate.dto.response.performance.RecommendedPerformanceResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.performances.PerformanceErrorCode;
import com.example.stagemate.repository.performance.PerformanceRepository;
import com.example.stagemate.repository.performance.PerformanceScheduleRepository;
import com.example.stagemate.repository.performance.PerformanceScrapRepository;
import com.example.stagemate.repository.performance.PerformanceStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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


    public PagedResponse<PerformanceDetailResponse> findFillteredPerformances(
            PerformanceType performanceType,
            PerformanceGenre performanceGenre,
            List<String> region,
            LocalDate date,
            Pageable pageable
    ) {
        Page<Performance> performancePage;

        if (performanceType == null && performanceGenre == null && region == null && date == null) {
            performancePage = performanceRepository.findAll(pageable);
        } else {
            performancePage = performanceRepository.findByCondition(
                    performanceType, performanceGenre, region, date, pageable
            );
        }

        List<PerformanceDetailResponse> performanceDetailResponses =
                performancePage.getContent().stream()
                        .map(PerformanceDetailResponse::from)
                        .collect(Collectors.toList());

        return PagedResponse.from(performanceDetailResponses, performancePage);
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
                .scrapDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        performanceScrapRepository.save(performanceScrap);
    }

    //추천 공연은 1시간이내 스크랩이 많이 오른 순으로 추천
    public PagedResponse<RecommendedPerformanceResponse> getRecommendPerformances(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        //increased_scrap_count 높은 순 공연통계 찾기
        Page<PerformanceStatistics> performanceStatistics =
                performanceStatisticsRepository.findTopByIncreasedScrapCount(pageable);

        return PagedResponse.from(
                performanceStatistics.getContent().stream()
                        .map(RecommendedPerformanceResponse::from)
                        .toList(),
                performanceStatistics
        );

    }


    public List<EventResponse> getAllPerformanceEventsForElkInit() {
        List<Performance> performances = performanceRepository.findAll();

        List<Event> events = performances.stream()
                // 람다를 사용해 "created" 또는 "updated" 등 필요한 이벤트 타입을 직접 전달합니다.
                .map(performance -> performance.toEvent("created"))
                .toList();

        return events.stream()
                .map(EventResponse::from)
                .toList();
    }


}
