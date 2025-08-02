package com.example.stagemate.domain.performanceSchedule;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.PerformanceScheduleCreateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "performance_schedules")
public class PerformanceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = true)
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    private String title;

    private String content;

    private String url;

    private LocalDate scheduleDate;

    private LocalDateTime scheduleStartTime;

    private LocalDateTime scheduleEndTime;

    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    private PerformanceScheduleType performanceScheduleType;

    @Enumerated(EnumType.STRING)
    private PerformanceScheduleReportStatus performanceScheduleReportStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity userJpaEntity;


    @OneToMany(mappedBy = "performanceSchedule", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    List<PerformanceScheduleReportCategory> performanceScheduleReportCategories = new ArrayList<>();


    //초기 공연일정 제보 되면 초기상태
    public void initReportStatus() {
        this.performanceScheduleReportStatus = PerformanceScheduleReportStatus.PENDING;
    }

    public void changeReportStatus(PerformanceScheduleReportStatus performanceScheduleReportStatus) {
        this.performanceScheduleReportStatus = performanceScheduleReportStatus;
    }

    //인터파크 공연일정이 바뀌었을때 scheduleDate 변경
    public void updateDateIfperformanceDateIsChanged(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public static PerformanceSchedule createPerformanceSchedule(
            UserJpaEntity user,
            PerformanceScheduleCreateRequest performanceScheduleCreateRequest,
            Performance performance,
            Theater theater) {

        PerformanceSchedule performanceSchedule =
                PerformanceSchedule
                        .builder()
                        .performance(performance)
                        .title(performanceScheduleCreateRequest.getTitle())
                        .content(performanceScheduleCreateRequest.getContent())
                        .url(performanceScheduleCreateRequest.getUrl())
                        .scheduleDate(performanceScheduleCreateRequest.getScheduleDate())
                        .scheduleStartTime(performanceScheduleCreateRequest.getScheduleDateStartTime())
                        .scheduleEndTime(performanceScheduleCreateRequest.getScheduleDateEndTime())
                        .reportDate(performanceScheduleCreateRequest.getReportDate())
                        .performanceScheduleType(PerformanceScheduleType.ETC)
                        .userJpaEntity(user)
                        .theater(theater)
                        .build();


        //초기상태인 PENDING로 초기화 후 저장
        performanceSchedule.initReportStatus();
        return performanceSchedule;
    }
}

