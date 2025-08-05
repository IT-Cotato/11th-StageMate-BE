package com.example.stagemate.service.notification;

import com.example.stagemate.domain.notification.Notification;
import com.example.stagemate.domain.notification.NotificationType;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.NotificationResponse;
import com.example.stagemate.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    public final NotificationRepository notificationRepository;

    public void save(UserJpaEntity user, NotificationType notificationType, Long targetId, String comment) {
        Notification.of(user, notificationType, targetId);
    }

    public List<NotificationResponse> getMyNotifications(UserJpaEntity user) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId());

        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
