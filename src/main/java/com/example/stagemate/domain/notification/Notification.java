package com.example.stagemate.domain.notification;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private UserJpaEntity receiver;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private Long targetId;
    private String content;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Notification of(UserJpaEntity receiver, NotificationType type, Long targetId, String comment) {
        return Notification.builder()
                .receiver(receiver)
                .type(type)
                .targetId(targetId)
                .content(comment)
                .build();
    }

}
