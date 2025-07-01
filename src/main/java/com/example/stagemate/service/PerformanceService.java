package com.example.stagemate.service;

import com.example.stagemate.domain.Performances;
import com.example.stagemate.global.code.status.PerformanceStatus;
import com.example.stagemate.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;


    //현재시간기준으로 상태업데이트
    @Transactional
    public void updatePerformanceStatusBasedOnCurrentDate() {
        List<Performances> ongoingPerformances =
                performanceRepository.findByPerformanceStatus(PerformanceStatus.ONGOING);

        for (Performances performance : ongoingPerformances) {
            performance.updateStatusBasedOnCurrentDate();
            performanceRepository.save(performance);
        }

    }



    @Transactional
    public void updateBatch(List<Performances> crawledPerformances) {
        //상영중, 상영예정 공연 가져오기
        List<Performances> existingPerformances = performanceRepository.findByPerformanceStatusIn(
                List.of(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING));

        //PerformanceId로 map 생성
        Map<String, Performances> existingMap = existingPerformances.stream()
                .collect(Collectors.toMap(Performances::getInterparkPerformanceId, performance -> performance));

        //크롤링한 공연 ID 집합
        Set<String> crawledPerformanceIds = crawledPerformances.stream()
                .map(Performances::getInterparkPerformanceId)
                .collect(Collectors.toSet());

        // 없으면 삽입, 존재하면 바뀐부분 업데이트
        for (Performances crawledPerformance : crawledPerformances) {
            String performanceId = crawledPerformance.getInterparkPerformanceId();
            if (existingMap.containsKey(performanceId)) {
                // Update existing performance
                Performances existing = existingMap.get(performanceId);
                existing.updateFromCrawledData(crawledPerformance);
                performanceRepository.save(existing);
            } else {
                // Insert new performance
                performanceRepository.save(crawledPerformance);
            }
        }

//        // 2. Find and cancel performances that are in DB but not in crawled data
//        List<Performances> toBeCancelled = existingPerformances.stream()
//                .filter(p -> !crawledPerformanceIds.contains(p.getPerformanceId()))
//                .peek(p -> p.cancelPerformance()) // Assuming you have a cancel method
//                .collect(Collectors.toList());
//
//        if (!toBeCancelled.isEmpty()) {
//            performanceRepository.saveAll(toBeCancelled);
//        }
    }

    public List<Performances> findOngoingPerformances() {
        //상영중인 공연 가져오기
        return performanceRepository.findByPerformanceStatus(PerformanceStatus.ONGOING);
    }
        




}
