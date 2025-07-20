package com.example.stagemate.service.performance;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleErrorCode;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleScrap;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportStatus;
import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.PerformanceScheduleCreateRequest;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.performances.PerformanceErrorCode;
import com.example.stagemate.repository.PerformanceRepository;
import com.example.stagemate.repository.PerformanceScheduleRepository;
import com.example.stagemate.repository.PerformanceScheduleScrapRepository;

import com.example.stagemate.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceScheduleService {
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final PerformanceScheduleScrapRepository performanceScheduleScrapRepository;
    private final TheaterRepository theaterRepository;
    private final PerformanceRepository performanceRepository;

    public void changePerformanceScheduleStatus(Long performanceScheduleId, PerformanceScheduleReportStatus performanceScheduleReportStatus) {
        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId).
                orElseThrow(() -> new AppException(PerformanceScheduleErrorCode.NOT_FOUND));

        performanceSchedule.changeReportStatus(performanceScheduleReportStatus);
    }

    public Long createPerformanceSchedule(UserJpaEntity user,
                                          PerformanceScheduleCreateRequest performanceScheduleCreateRequest) {

        Performance performance = performanceRepository.findById(performanceScheduleCreateRequest.getPerformanceId())
                .orElseThrow(() -> new AppException(PerformanceErrorCode.NOT_FOUND));

        Theater theater = theaterRepository.findById(performanceScheduleCreateRequest.getTheaterId())
                .orElseThrow(() -> new AppException(PerformanceErrorCode.NOT_FOUND));

        PerformanceSchedule performanceSchedule = PerformanceSchedule.createPerformanceSchedule(user, performanceScheduleCreateRequest, performance);

        return performanceScheduleRepository.save(performanceSchedule).getId();
    }

    //스크랩 여부
    public boolean findIsScraped(Long performanceScheduleId, UserJpaEntity user) {
        if (user == null) {
            return false;
        }

        return performanceScheduleScrapRepository.existsByPerformanceScheduleIdAndUserId(performanceScheduleId, user.getId());
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

    //공연 스케줄 상세 정보
    public PerformanceSchedule getPerformanceSchedule(Long performanceScheduleId) {
        return performanceScheduleRepository.findByIdWithPerformanceScheduleReportCategories(performanceScheduleId)
                .orElseThrow(() -> new AppException(PerformanceScheduleErrorCode.NOT_FOUND));
    }

    //공연 스케줄 상태별 조회
    public List<PerformanceSchedule> getPerformanceSchedules(List<PerformanceScheduleReportStatus> performanceScheduleReportStatus) {
        return performanceScheduleRepository.findByScheduleReportStatus(performanceScheduleReportStatus);
    }

    //공연 스케줄 스크랩 저장 또는 삭제
    public void insertOrDeletePerformanceScheduleScrap(Long performanceScheduleId , UserJpaEntity user) {
        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
                .orElseThrow(() -> new AppException(PerformanceScheduleErrorCode.NOT_FOUND));

        boolean isExist =
                performanceScheduleScrapRepository.existsByPerformanceScheduleIdAndUserId(performanceScheduleId, user.getId());

        if (isExist) {
            performanceScheduleScrapRepository.deleteByPerformanceScheduleIdAndUserId(performanceScheduleId, user.getId());
            return;
        }

        PerformanceScheduleScrap performanceScheduleScrap =
                PerformanceScheduleScrap.createPerformanceScheduleScrap(performanceSchedule, user);

        performanceScheduleScrapRepository.save(performanceScheduleScrap);
    }

    //공연 스케줄 스크랩 삭제
    public void deletePerformanceScheduleScrap(Long performanceScheduleScrapId) {
        performanceScheduleScrapRepository.deleteById(performanceScheduleScrapId);
    }

    //공연 스케줄 스크랩 리스트
    public List<PerformanceScheduleScrap> getPerformanceScheduleScrapList(Long userId) {
        return performanceScheduleScrapRepository.findByUserId(userId);
    }



}
