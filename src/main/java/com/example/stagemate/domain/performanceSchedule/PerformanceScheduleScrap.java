package com.example.stagemate.domain.performanceSchedule;


import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "performance_schedule_scraps")
public class PerformanceScheduleScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_schedule_scrap_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @ManyToOne
    @JoinColumn(name = "performance_schedule_id")
    private PerformanceSchedule performanceSchedule;

    public static PerformanceScheduleScrap createPerformanceScheduleScrap(
            PerformanceSchedule performanceSchedule, UserJpaEntity user) {
        return PerformanceScheduleScrap.builder()
                .performanceSchedule(performanceSchedule)
                .user(user)
                .build();
    }
}
