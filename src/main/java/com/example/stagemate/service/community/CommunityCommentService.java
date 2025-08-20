package com.example.stagemate.service.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.notification.NotificationType;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityCommentRequest;
import com.example.stagemate.dto.request.community.CommunityCommentUpdateRequest;
import com.example.stagemate.dto.response.community.CommunityCommentResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.community.CommunityCommentRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.community.UserBlockRepository;
import com.example.stagemate.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final UserBlockRepository userBlockRepository;
    private final NotificationService notificationService;

    public List<CommunityCommentResponse> getCommentsByPost(CommunityPost post, UserJpaEntity user) {
        List<CommunityComment> rootComments = commentRepository.findRootCommentsWithChildrenByPost(post);
        Set<Long> blockedUserIds = (user == null)
                ? Set.of()
                : userBlockRepository.findBlockedUserIdsByBlockerId(user.getId());
        return rootComments.stream()
                .map(comment -> new CommunityCommentResponse(comment, blockedUserIds))
                .toList();
    }

    // 커뮤니티 게시글의 댓글 수 증가
    // 커뮤니티 게시글의 댓글 수 증가 및 알림 기능
    public void addComment(UserJpaEntity user, Long postId, CommunityCommentRequest request) {
        request.validate();

        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));
        if (post.isDeleted()) {
            throw new AppException(COMMUNITY_POST_NOT_FOUND);
        }

        CommunityComment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
            if (parent.getParent() != null) {
                throw new AppException(COMMUNITY_REPLY_NOT_ALLOWED);
            }
        }

        CommunityComment comment = request.toEntity(post, user, parent);
        commentRepository.save(comment);

        // 알림 로직 처리
        handleNotifications(user, post, comment, parent);

        post.addCommentCount();
    }

    private void handleNotifications(UserJpaEntity user, CommunityPost post, CommunityComment comment, CommunityComment parent) {
        Long currentUserId = user.getId();
        Long postAuthorId = post.getAuthor().getId();

        if (parent == null) {
            // 새로운 댓글일 경우
            // 자신의 게시글에 직접 댓글을 다는 경우가 아닐 때만 알림
            if (!postAuthorId.equals(currentUserId)) {
                notificationService.save(
                        post.getAuthor(),
                        NotificationType.COMMENT_ON_POST,
                        post.getId(),
                        comment.getContent()
                );
            }
        } else {
            // 대댓글
            Long parentCommentAuthorId = parent.getUser().getId();

            // 부모 댓글 작성자에게 알림
            // 자기 자신에게는 보내지 않음
            if (!parentCommentAuthorId.equals(currentUserId)) {
                notificationService.save(
                        parent.getUser(),
                        NotificationType.REPLY_ON_COMMENT,
                        post.getId(),
                        comment.getContent()
                );
            }

            // 게시글 작성자에게 알림
            //
            if (!postAuthorId.equals(currentUserId) && !postAuthorId.equals(parentCommentAuthorId)) {
                notificationService.save(
                        post.getAuthor(),
                        NotificationType.REPLY_ON_COMMENT,
                        post.getId(),
                        comment.getContent()
                );
            }
        }
    }


    public void updateComment(UserJpaEntity user, Long commentId, CommunityCommentUpdateRequest request) {
        request.validate();
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
        if(comment.isDeleted())
            throw new AppException(COMMUNITY_COMMENT_NOT_FOUND);
        if (!comment.getUser().getId().equals(user.getId()))
            throw new AppException(COMMUNITY_COMMENT_NOT_AUTHOR);
        comment.updateContent(request.content());
    }

    public void deleteComment(UserJpaEntity user, Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
        if(comment.isDeleted())
            throw new AppException(COMMUNITY_COMMENT_NOT_FOUND);
        if (!comment.getUser().getId().equals(user.getId()))
            throw new AppException(COMMUNITY_COMMENT_NOT_AUTHOR);
        comment.deleteComment();
    }
}
