package com.example.stagemate.service.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.community.TargetType;
import com.example.stagemate.domain.community.UserBlock;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.UserBlockStatusResponse;
import com.example.stagemate.dto.response.community.UserBlockListResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.community.CommunityCommentRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.community.UserBlockRepository;
import com.example.stagemate.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.stagemate.global.exception.CommonErrorCode.NOT_FOUND_USER;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository commentRepository;
    private final UserJpaRepository userRepository;

    public void blockUser(UserJpaEntity user, Long blockedUserId) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        UserJpaEntity blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        
        //본인 차단 불가
        if(user.getId().equals(blockedUser.getId())) {
            throw new AppException(INVALID_BLOCK_SELF);
        }

        //이미 차단했는지 여부 확인
        boolean isBlocked = userBlockRepository.existsByBlockerIdAndBlockedId(user.getId(), blockedUserId);
        if (isBlocked) {
            throw new AppException(USER_ALREADY_BLOCKED);
        }

        UserBlock userBlock = UserBlock.of(user, blockedUser);
        userBlockRepository.save(userBlock);
    }

    public void blockUser(UserJpaEntity user, Long targetId, String targetTypeRaw) {
        TargetType targetType;
        try {
            targetType = TargetType.valueOf(targetTypeRaw);
        } catch (IllegalArgumentException e) {
            throw new AppException(REPORT_TARGET_TYPE_INVALID);
        }

        UserJpaEntity targetUser;
        userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        // 게시글/댓글 작성자 추출
        switch (targetType) {
            case POST -> {
                CommunityPost communityPost = communityRepository.findById(targetId)
                        .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));
                targetUser = communityPost.getAuthor();
            }
            case COMMENT -> {
                CommunityComment communityComment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
                targetUser = communityComment.getUser();
            }
            default -> throw new AppException(REPORT_TARGET_TYPE_INVALID);
        }

        // 본인 차단 방지
        if (user.getId().equals(targetUser.getId())) {
            throw new AppException(INVALID_BLOCK_SELF);
        }

        // 중복 차단 방지
        boolean alreadyBlocked = userBlockRepository.existsByBlockerIdAndBlockedId(user.getId(), targetUser.getId());
        if (alreadyBlocked) {
            throw new AppException(USER_ALREADY_BLOCKED);
        }

        UserBlock userBlock = UserBlock.of(user, targetUser);
        userBlockRepository.save(userBlock);
    }


    public void unblockUser(UserJpaEntity user, Long blockedUserId) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        UserJpaEntity blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        userBlockRepository.deleteByBlockerIdAndBlockedId(user.getId(), blockedUser.getId());
    }


    public PagedResponse<UserBlockListResponse> getBlockedUsers(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        Page<UserBlock> allByBlockerId = userBlockRepository.findAllByBlockerId(user.getId(), pageable);
        List<UserBlockListResponse> list = allByBlockerId
                .stream()
                .map(UserBlockListResponse::from)
                .toList();
        return PagedResponse.from(list, allByBlockerId);

    }

    //현재 사용자 기준으로 userIds중에 차단된 사용자가 있는지 확인
    public List<UserBlockStatusResponse> checkBlockedUser(Long userId, List<Long> targetUserIds) {
        List<Long> blokedUserIds = userBlockRepository.findBlockedUserIds(userId, targetUserIds);

        List<UserBlockStatusResponse> result = new ArrayList<>();

        for (Long id : targetUserIds) {
            if (blokedUserIds.contains(id)) {
                result.add(new UserBlockStatusResponse(id, true));
            } else {
                result.add(new UserBlockStatusResponse(id, false));
            }
        }

        return result;
    }
}

