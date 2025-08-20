package com.example.stagemate.service.performance;

import com.example.stagemate.domain.performance.PerformanceType;
import com.example.stagemate.domain.performanceSchedule.*;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceStatus;
import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.dto.data.CrawledPerformanceInfo;
import com.example.stagemate.repository.performance.PerformanceRepository;
import com.example.stagemate.repository.performance.PerformanceScheduleReportCategoryRepository;
import com.example.stagemate.repository.performance.PerformanceScheduleRepository;
import com.example.stagemate.repository.TheaterRepository;
import com.example.stagemate.service.event.EventService;
import com.example.stagemate.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PerformanceCrawlingBatchService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final TheaterRepository theaterRepository;
    private final PerformanceScheduleReportCategoryRepository performanceScheduleReportCategoryRepository;
    private final EventService eventService;



    // 현재시간 기준으로 상태 업데이트
    @Transactional
    public void updatePerformanceStatusBasedOnCurrentDate() {
        List<Performance> ongoingPerformances =
                performanceRepository.findByPerformanceStatus(PerformanceStatus.ONGOING);

        for (Performance performance : ongoingPerformances) {
            performance.updateStatusBasedOnCurrentDate();

            if (performance.getPerformanceStatus().equals(PerformanceStatus.ENDED)) {
//                searchService.deleteFromPerformanceId(performance.getId());

                eventService.saveEvent(performance.toEvent("deleted"));
            }

            performanceRepository.save(performance);
        }
    }


    //크롤링해온 공연목록 insert or update
    @Transactional
    public void updateBatch(List<CrawledPerformanceInfo> crawledPerformances) {
        // 상영중, 상영예정 공연 가져오기
        List<Performance> existingPerformances = performanceRepository.findByPerformanceStatusIn(
                List.of(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING));

        // PerformanceId로 map 생성
        Map<String, Performance> existingMap = existingPerformances.stream()
                .collect(Collectors.toMap(Performance::getInterparkPerformanceId, performance -> performance));


        int i = 0;
        // 없으면 삽입, 존재하면 바뀐부분 업데이트
        for (CrawledPerformanceInfo crawledPerformanceInfo : crawledPerformances) {

            Optional<Theater> theater = theaterRepository.findByName(crawledPerformanceInfo.getTheaterName());

            //기존에 없는 극장이면 insert
            Theater selectedTheater;
            selectedTheater = theater.orElseGet(
                    () -> insertTheater(crawledPerformanceInfo.getTheaterName(), crawledPerformanceInfo.getRegion()));

            String performanceId = crawledPerformanceInfo.getInterparkPerformanceId();
            Performance crawledPerformance = Performance.from(crawledPerformanceInfo, selectedTheater);


            if (existingMap.containsKey(performanceId)) {
                // 기존에 존재하면 업데이트
                Performance existing = existingMap.get(performanceId);

                if(existing.isNotChanged(crawledPerformance)) {
                    //필드값이 변경되지않았으면 업데이트하지 않음
                    continue;
                }

                existing.updateFromCrawledData(crawledPerformance);

                //기존 공연의 시작일, 종료일이 변경되면 공연 스케줄 업데이트
                updatePerformanceSchedule(existing);

//                searchService.saveFromPerformance(existing);
                eventService.saveEvent(existing.toEvent("updated"));

                performanceRepository.save(existing);
            } else {
                // 새로운 공연이면 삽입
                performanceRepository.save(crawledPerformance);

                //새로운 공연이면 공연 시작 스케줄 + 공연 종료 스케줄 2개 추가
                insertPerformanceSchedule(crawledPerformance);

//                searchService.saveFromPerformance(crawledPerformance);
                eventService.saveEvent(crawledPerformance.toEvent("created"));
            }
        }

        // 주석 처리된 취소 로직 (필요 시 복구)
        // List<Performance> toBeCancelled = existingPerformances.stream()
        //         .filter(p -> !crawledPerformanceIds.contains(p.getInterparkPerformanceId()))
        //         .peek(p -> p.cancelPerformance()) // Assuming you have a cancel method
        //         .collect(Collectors.toList());

        // if (!toBeCancelled.isEmpty()) {
        //     performanceRepository.saveAll(toBeCancelled);
        // }
    }

    private Theater insertTheater(String theaterName, String region) {

        Theater theater = Theater.builder()
                .name(theaterName)
                .region(region)
                .build();
        return theaterRepository.save(theater);
    }

    private PerformanceSchedule createPerformanceStartOrEndSchedule(Performance performance, PerformanceScheduleType scheduleType) {
        LocalDate date = scheduleType == PerformanceScheduleType.START ?
                performance.getStartDate() : performance.getEndDate();

        String content = scheduleType == PerformanceScheduleType.START ? "첫 공연" : "마지막 공연";

        LocalDateTime dateTime = date.atStartOfDay();  // or customize by type

        PerformanceSchedule performanceSchedule = PerformanceSchedule.builder()
                .performance(performance)
                .content(content)
                .scheduleDate(date)
                .performanceScheduleType(scheduleType)
                .scheduleStartTime(dateTime)
                .scheduleEndTime(dateTime)
                .theater(performance.getTheater())
                .reportDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .performanceScheduleReportStatus(PerformanceScheduleReportStatus.APPROVED)
                .title(performance.getPerformanceName() + " " + content)
                .url(performance.getUrl())
                .build();

        PerformanceSchedule savedSchedule = performanceScheduleRepository.save(performanceSchedule);

        PerformanceScheduleReportCategoryType categoryType =
                performance.getPerformanceType() == PerformanceType.PLAY ?
                        PerformanceScheduleReportCategoryType.PLAY :
                        PerformanceScheduleReportCategoryType.MUSICAL;

        PerformanceScheduleReportCategory category = PerformanceScheduleReportCategory.builder()
                .performanceSchedule(savedSchedule)
                .performanceScheduleReportCategoryType(categoryType)
                .categoryOrder(1)
                .build();

        performanceScheduleReportCategoryRepository.save(category);

        return savedSchedule;
    }

    //기존 공연의 시작일, 종료일이 변경되면 공연 스케줄 업데이트
    //공연 시작 스케줄, 공연 종료 스케줄 둘다 업데이트
    private void updatePerformanceSchedule(Performance updatedPerformance) {
        // Optional로 감싸기
        Optional<PerformanceSchedule> startScheduleOpt = performanceScheduleRepository
                .findOptionalByPerformance_IdAndPerformanceScheduleType(
                        updatedPerformance.getId(), PerformanceScheduleType.START);

        Optional<PerformanceSchedule> endScheduleOpt = performanceScheduleRepository
                .findOptionalByPerformance_IdAndPerformanceScheduleType(
                        updatedPerformance.getId(), PerformanceScheduleType.END);

        // 시작 스케줄 업데이트
        startScheduleOpt.ifPresent(schedule -> {
            if (!schedule.getScheduleDate().equals(updatedPerformance.getStartDate())) {
                log.info("첫 공연 스케줄 UPDATE 발생 -----------------------------------------------");
                schedule.updateDateIfperformanceDateIsChanged(updatedPerformance.getStartDate());
                performanceScheduleRepository.save(schedule);
            }
        });

        // 종료 스케줄 업데이트
        endScheduleOpt.ifPresent(schedule -> {
            if (!schedule.getScheduleDate().equals(updatedPerformance.getEndDate())) {
                log.info("마지막 공연 스케줄 UPDATE 발생 -----------------------------------------------");
                schedule.updateDateIfperformanceDateIsChanged(updatedPerformance.getEndDate());
                performanceScheduleRepository.save(schedule);
            }
        });
    }

    private void insertPerformanceSchedule(Performance performance) {
        // Optional 로 시작/종료 스케줄 조회
        Optional<PerformanceSchedule> startScheduleOpt = performanceScheduleRepository
                .findOptionalByPerformance_IdAndPerformanceScheduleType(performance.getId(), PerformanceScheduleType.START);

        Optional<PerformanceSchedule> endScheduleOpt = performanceScheduleRepository
                .findOptionalByPerformance_IdAndPerformanceScheduleType(performance.getId(), PerformanceScheduleType.END);

        // 시작 스케줄이 없으면 새로 추가
        if (startScheduleOpt.isEmpty()) {
            PerformanceSchedule startSchedule =
                    createPerformanceStartOrEndSchedule(performance, PerformanceScheduleType.START);
            performanceScheduleRepository.save(startSchedule);
        }

        // 종료 스케줄이 없으면 새로 추가
        if (endScheduleOpt.isEmpty()) {
            PerformanceSchedule endSchedule =
                    createPerformanceStartOrEndSchedule(performance, PerformanceScheduleType.END);
            performanceScheduleRepository.save(endSchedule);
        }
    }


}
