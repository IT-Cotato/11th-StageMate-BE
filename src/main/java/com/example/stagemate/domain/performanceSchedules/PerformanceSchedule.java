package com.example.stagemate.domain.performanceSchedules;

import com.example.stagemate.domain.performances.Performance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "performance_schedules")
public class PerformanceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceScheduleId;

    @ManyToOne
    private Performance performance;

    private String content;

    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    private PerformanceScheduleType performanceScheduleType;

    public void updateDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
