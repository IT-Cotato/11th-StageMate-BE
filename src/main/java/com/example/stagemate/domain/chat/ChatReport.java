package com.example.stagemate.domain.chat;

import com.example.stagemate.domain.community.ReportReason;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private UserJpaEntity reporter;

    private String chatId;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static ChatReport of(UserJpaEntity user, String chatId, ReportReason reason) {
        return ChatReport.builder()
                .reporter(user)
                .chatId(chatId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
