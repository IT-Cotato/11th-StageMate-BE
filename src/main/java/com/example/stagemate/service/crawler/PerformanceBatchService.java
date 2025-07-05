package com.example.stagemate.service.crawler;

import com.example.stagemate.domain.performanceSchedules.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedules.PerformanceScheduleType;
import com.example.stagemate.domain.performances.Performance;
import com.example.stagemate.domain.performances.PerformanceStatus;
import com.example.stagemate.repository.PerformanceRepository;
import com.example.stagemate.repository.PerformanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PerformanceBatchService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;


    // 현재시간 기준으로 상태 업데이트
    @Transactional
    public void updatePerformanceStatusBasedOnCurrentDate() {
        List<Performance> ongoingPerformances =
                performanceRepository.findByPerformanceStatus(PerformanceStatus.ONGOING);

        for (Performance performance : ongoingPerformances) {
            performance.updateStatusBasedOnCurrentDate();
            performanceRepository.save(performance);
        }
    }


    //크롤링해온 공연목록 insert or update
    @Transactional
    public void updateBatch(List<Performance> crawledPerformances) {
        // 상영중, 상영예정 공연 가져오기
        List<Performance> existingPerformances = performanceRepository.findByPerformanceStatusIn(
                List.of(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING));

        // PerformanceId로 map 생성
        Map<String, Performance> existingMap = existingPerformances.stream()
                .collect(Collectors.toMap(Performance::getInterparkPerformanceId, performance -> performance));

        // 크롤링한 공연 ID 집합
        Set<String> crawledPerformanceIds = crawledPerformances.stream()
                .map(Performance::getInterparkPerformanceId)
                .collect(Collectors.toSet());

        // 없으면 삽입, 존재하면 바뀐부분 업데이트
        for (Performance crawledPerformance : crawledPerformances) {
            String performanceId = crawledPerformance.getInterparkPerformanceId();
            if (existingMap.containsKey(performanceId)) {
                // 기존에 존재하면 업데이트
                Performance existing = existingMap.get(performanceId);

                //기존 공연의 시작일, 종료일이 변경되면 공연 스케줄 업데이트
                updatePerformanceSchedule(existing);

                existing.updateFromCrawledData(crawledPerformance);
                performanceRepository.save(existing);
            } else {
                // 새로운 공연이면 삽입
                performanceRepository.save(crawledPerformance);

                //새로운 공연이면 공연 시작 스케줄 + 공연 종료 스케줄 2개 추가
                insertPerformanceSchedule(crawledPerformance);
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

    private PerformanceSchedule createPerformanceStartOrEndSchedule(Performance performance, LocalDate date) {
        PerformanceScheduleType scheduleType = date.equals(performance.getStartDate()) ?
                PerformanceScheduleType.START : PerformanceScheduleType.END;

        String content = scheduleType.equals(PerformanceScheduleType.START) ?
                "첫 공연" : "마지막 공연";

        return PerformanceSchedule.builder()
                .performance(performance)
                .content(content)
                .scheduleDate(date)
                .performanceScheduleType(scheduleType)
                .build();

    }

    //기존 공연의 시작일, 종료일이 변경되면 공연 스케줄 업데이트
    //공연 시작 스케줄, 공연 종료 스케줄 둘다 업데이트
    private void updatePerformanceSchedule(Performance newPerformance) {
        //기존 공연의 첫 공연 스케줄 찾기
        PerformanceSchedule existingPerformanceStartSchedule = performanceScheduleRepository
                .findByPerformance_PerformanceIdAndPerformanceScheduleType(
                        newPerformance.getPerformanceId(), PerformanceScheduleType.START);

        //기존 공연의 마지막 공연 스케줄 찾기
        PerformanceSchedule existingPerformanceEndSchedule = performanceScheduleRepository
                .findByPerformance_PerformanceIdAndPerformanceScheduleType(
                        newPerformance.getPerformanceId(), PerformanceScheduleType.END);


        //시작날짜가 변경됐으면 시작 스케줄 업데이트
        if(!existingPerformanceStartSchedule.getScheduleDate().equals(newPerformance.getStartDate())) {
            existingPerformanceStartSchedule.updateDate(newPerformance.getStartDate());
            performanceScheduleRepository.save(existingPerformanceStartSchedule);
        }

        //종료날짜가 변경됐면 종료 스케줄 업데이트
        if(!existingPerformanceEndSchedule.getScheduleDate().equals(newPerformance.getEndDate())) {
            existingPerformanceEndSchedule.updateDate(newPerformance.getEndDate());
            performanceScheduleRepository.save(existingPerformanceEndSchedule);
        }
    }

    //새로운 공연이 insert -> 공연 시작 스케줄 + 공연 종료 스케줄 2개 추가
    private void insertPerformanceSchedule(Performance performance) {
        PerformanceSchedule performanceStartSchedule =
                createPerformanceStartOrEndSchedule(performance, performance.getStartDate());

        PerformanceSchedule performanceEndSchedule =
                createPerformanceStartOrEndSchedule(performance, performance.getEndDate());

        performanceScheduleRepository.save(performanceStartSchedule);
        performanceScheduleRepository.save(performanceEndSchedule);

    }

}
