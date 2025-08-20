package com.example.stagemate.domain.chat;

import com.example.stagemate.domain.community.ReportReason;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "chat_reports")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private UserJpaEntity reporter;

    private String chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private UserJpaEntity targetUser;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

    public static ChatReport of(UserJpaEntity user, UserJpaEntity targetUser, String chatId, ReportReason reason) {
        return ChatReport.builder()
                .targetUser(targetUser)
                .reporter(user)
                .chatId(chatId)
                .reason(reason)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }
}
