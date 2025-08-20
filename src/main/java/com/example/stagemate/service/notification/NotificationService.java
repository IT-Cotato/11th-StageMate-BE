package com.example.stagemate.service.notification;

import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.notification.Notification;
import com.example.stagemate.domain.notification.NotificationType;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.NotificationResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    public final NotificationRepository notificationRepository;
    public final CommunityRepository communityRepository;

    public void save(UserJpaEntity user, NotificationType notificationType, Long targetId, String comment) {
        Notification notification = Notification.of(user, notificationType, targetId, comment);
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(UserJpaEntity user) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId());

        return notifications.stream()
                .map(notification -> {
                    CommunityPost communityPost = communityRepository.findById(notification.getTargetId())
                            .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));
                    return NotificationResponse.from(notification,communityPost.getCategory().getDescription());
                })
                .toList();
    }
}
