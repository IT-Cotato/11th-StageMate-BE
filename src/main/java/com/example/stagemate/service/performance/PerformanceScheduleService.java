package com.example.stagemate.service.performance;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleErrorCode;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportStatus;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleScrap;
import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.domain.theater.TheaterErrorCode;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.PerformanceScheduleCreateRequest;
import com.example.stagemate.dto.response.performance.PerformanceScheduleDetailResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.performances.PerformanceErrorCode;
import com.example.stagemate.repository.performance.PerformanceRepository;
import com.example.stagemate.repository.performance.PerformanceScheduleRepository;
import com.example.stagemate.repository.performance.PerformanceScheduleScrapRepository;
import com.example.stagemate.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .orElseThrow(() -> new AppException(TheaterErrorCode.THEATER_NOT_FOUND));

        PerformanceSchedule performanceSchedule =
                PerformanceSchedule.createPerformanceSchedule(user, performanceScheduleCreateRequest, performance, theater);

        return performanceScheduleRepository.save(performanceSchedule).getId();
    }


    //스크랩 여부
    private boolean findIsScraped(Long performanceScheduleId, UserJpaEntity user) {
        if (user == null) {
            return false;
        }

        return performanceScheduleScrapRepository.existsByPerformanceScheduleIdAndUserId(performanceScheduleId, user.getId());
    }

    // ====== 리스트 반환 (월간) ======
    public List<PerformanceScheduleDetailResponse> getPerformanceSchedule(
            UserJpaEntity user, Integer year, Integer month,
            PerformanceScheduleReportStatus performanceScheduleReportStatus) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<PerformanceSchedule> schedules =
                performanceScheduleRepository.findByScheduleDateBetween(
                        startDate, endDate, performanceScheduleReportStatus);

        // user가 null이면 전부 false
        if (user == null || schedules.isEmpty()) {
            return schedules.stream()
                    .map(ps -> PerformanceScheduleDetailResponse.from(ps, false))
                    .toList();
        }

        // 배치 조회로 스크랩 여부 계산
        List<Long> ids = schedules.stream().map(PerformanceSchedule::getId).toList();
        Set<Long> scrapedIdSet = new HashSet<>(
                performanceScheduleScrapRepository.findScrapedScheduleIdsByUser(user.getId(), ids)
        );

        return schedules.stream()
                .map(ps -> PerformanceScheduleDetailResponse.from(ps, scrapedIdSet.contains(ps.getId())))
                .toList();
    }

    // ====== 리스트 반환 (일간) ======
    public List<PerformanceScheduleDetailResponse> getPerformanceSchedule(
            UserJpaEntity user, Integer year, Integer month, Integer day,
            PerformanceScheduleReportStatus performanceScheduleReportStatus) {

        LocalDate date = LocalDate.of(year, month, day);

        List<PerformanceSchedule> schedules =
                performanceScheduleRepository.findByScheduleDateAndPerformanceScheduleReportStatus(
                        date, performanceScheduleReportStatus);

        // user가 null이면 전부 false
        if (user == null || schedules.isEmpty()) {
            return schedules.stream()
                    .map(ps -> PerformanceScheduleDetailResponse.from(ps, false))
                    .toList();
        }

        // 배치 조회로 스크랩 여부 계산
        List<Long> ids = schedules.stream().map(PerformanceSchedule::getId).toList();
        Set<Long> scrapedIdSet = new HashSet<>(
                performanceScheduleScrapRepository.findScrapedScheduleIdsByUser(user.getId(), ids)
        );

        return schedules.stream()
                .map(ps -> PerformanceScheduleDetailResponse.from(ps, scrapedIdSet.contains(ps.getId())))
                .toList();
    }




    //공연 스케줄 상세 정보
    public PerformanceScheduleDetailResponse getPerformanceSchedule(UserJpaEntity user, Long performanceScheduleId) {
        PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(performanceScheduleId)
                .orElseThrow(() -> new AppException(PerformanceScheduleErrorCode.NOT_FOUND));

        boolean isScraped = findIsScraped(performanceScheduleId, user);

        return PerformanceScheduleDetailResponse.from(performanceSchedule, isScraped);
    }

    //공연 스케줄 상태별 조회
    public List<PerformanceScheduleDetailResponse> getPerformanceSchedules(List<PerformanceScheduleReportStatus> performanceScheduleReportStatus) {
        List<PerformanceSchedule> performanceSchedules =
                performanceScheduleRepository.findByScheduleReportStatus(performanceScheduleReportStatus);

        List<Boolean> isScraped =
                performanceSchedules.stream().map(performanceSchedule -> findIsScraped(performanceSchedule.getId(), null)).toList();

        return performanceSchedules.stream()
                .map(performanceSchedule -> PerformanceScheduleDetailResponse.from(performanceSchedule, isScraped.get(performanceSchedules.indexOf(performanceSchedule))))
                .toList();
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



    // ====== 페이지네이션 (월간) ======
    public PagedResponse<PerformanceScheduleDetailResponse> getPerformanceScheduleV2(
            UserJpaEntity user, Integer year, Integer month,
            PerformanceScheduleReportStatus performanceScheduleReportStatus,
            Pageable pageable) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Page<PerformanceSchedule> page = performanceScheduleRepository
                .findByScheduleDateBetweenAndPerformanceScheduleReportStatus(
                        startDate, endDate, performanceScheduleReportStatus, pageable);

        // user가 null이면 전부 false
        if (user == null || page.isEmpty()) {
            Page<PerformanceScheduleDetailResponse> mapped =
                    page.map(ps -> PerformanceScheduleDetailResponse.from(ps, false));
            return PagedResponse.from(mapped.getContent(), page);
        }

        // 배치 조회
        List<Long> ids = page.getContent().stream().map(PerformanceSchedule::getId).toList();
        Set<Long> scrapedIdSet = ids.isEmpty()
                ? Set.of()
                : new HashSet<>(performanceScheduleScrapRepository.findScrapedScheduleIdsByUser(user.getId(), ids));

        Page<PerformanceScheduleDetailResponse> mapped =
                page.map(ps -> PerformanceScheduleDetailResponse.from(ps, scrapedIdSet.contains(ps.getId())));

        return PagedResponse.from(mapped.getContent(), page);
    }

    // ====== 페이지네이션 (일간) ======
    public PagedResponse<PerformanceScheduleDetailResponse> getPerformanceScheduleV2(
            UserJpaEntity user, Integer year, Integer month, Integer day,
            PerformanceScheduleReportStatus performanceScheduleReportStatus,
            Pageable pageable) {

        LocalDate date = LocalDate.of(year, month, day);

        Page<PerformanceSchedule> page = performanceScheduleRepository
                .findByScheduleDateAndPerformanceScheduleReportStatus(
                        date, performanceScheduleReportStatus, pageable);

        // user가 null이면 전부 false
        if (user == null || page.isEmpty()) {
            Page<PerformanceScheduleDetailResponse> mapped =
                    page.map(ps -> PerformanceScheduleDetailResponse.from(ps, false));
            return PagedResponse.from(mapped.getContent(), page);
        }

        // 배치 조회
        List<Long> ids = page.getContent().stream().map(PerformanceSchedule::getId).toList();
        Set<Long> scrapedIdSet = ids.isEmpty()
                ? Set.of()
                : new HashSet<>(performanceScheduleScrapRepository.findScrapedScheduleIdsByUser(user.getId(), ids));

        Page<PerformanceScheduleDetailResponse> mapped =
                page.map(ps -> PerformanceScheduleDetailResponse.from(ps, scrapedIdSet.contains(ps.getId())));

        return PagedResponse.from(mapped.getContent(), page);
    }

}
