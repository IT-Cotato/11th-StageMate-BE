package com.example.stagemate.domain.performanceSchedule;

import com.example.stagemate.domain.performance.Performance;
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
    @Column(name = "performance_schedule_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    private String content;

    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    private PerformanceScheduleType performanceScheduleType;

    public void updateDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
