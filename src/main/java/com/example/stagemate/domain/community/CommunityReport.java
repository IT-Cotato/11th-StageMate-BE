package com.example.stagemate.domain.community;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "report", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reporter_id", "targetType", "targetId"})
})
public class CommunityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private UserJpaEntity reporter;

    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType; // 게시글 또는 댓글

    private Long targetId;  // 게시글 ID 또는 댓글 ID

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    private LocalDateTime createdAt = LocalDateTime.now();

    public static CommunityReport of(UserJpaEntity user, ReportTargetType targetType, Long targetId, ReportReason reason) {
        return CommunityReport.builder()
                .reporter(user)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }
    

}

