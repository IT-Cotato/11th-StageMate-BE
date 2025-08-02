package com.example.stagemate.service.report;

import com.example.stagemate.domain.chat.Chat;
import com.example.stagemate.domain.chat.ChatErrorCode;
import com.example.stagemate.domain.chat.ChatReport;
import com.example.stagemate.domain.community.CommunityReport;
import com.example.stagemate.domain.community.ReportReason;
import com.example.stagemate.domain.community.TargetType;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.chat.ChatReportCountResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.chat.ChatReportRepository;
import com.example.stagemate.repository.chat.ChatRepository;
import com.example.stagemate.repository.community.CommunityCommentRepository;
import com.example.stagemate.repository.community.CommunityReportRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.stagemate.global.exception.CommonErrorCode.NOT_FOUND_USER;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ChatRepository chatRepository;
    private final ChatReportRepository chatReportRepository;
    private final CommunityReportRepository communityReportRepository;
    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserJpaRepository userRepository;

    // 커뮤니티 게시글/댓글 신고
    @Transactional
    public void reportCommunityPost(UserJpaEntity user, Long targetId, String targetTypeRaw, String reasonRaw) {
        TargetType targetType = TargetType.fromString(targetTypeRaw);
        ReportReason reason = ReportReason.fromString(reasonRaw);


        switch (targetType) {
            case POST -> communityRepository.findById(targetId)
                    .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));
            case COMMENT -> communityCommentRepository.findById(targetId)
                    .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
        }

        userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(NOT_FOUND_USER));

        // 중복 신고 방지
        boolean alreadyReported = communityReportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                user.getId(), targetType, targetId
        );
        if (alreadyReported) {
            throw new AppException(COMMUNITY_REPORT_ALREADY_EXISTS);
        }

        CommunityReport report = CommunityReport.of(user, targetType, targetId, reason);
        communityReportRepository.save(report);
    }

    // 채팅 신고
    public void reportChat(UserJpaEntity user, String chatId, String reasonRaw) {
        ReportReason reason = ReportReason.fromString(reasonRaw);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new AppException(ChatErrorCode.CHAT_NOT_FOUND));

        // 중복 신고 방지
        boolean alreadyReported = chatReportRepository.existsByReporterIdAndChatId(user.getId(), chatId);

        if (alreadyReported) {
            throw new AppException(ChatErrorCode.CHAT_REPORT_ALREADY_EXISTS);
        }


        ChatReport chatReport = ChatReport.of(user, chatId, reason);

        chatReportRepository.save(chatReport);

    }

    //채팅 신고 횟수 getChatReportCount
    public List<ChatReportCountResponse> getChatReportCount(UserJpaEntity user, List<Long> userIds) {
        userIds.forEach(id -> {
            if (!userRepository.existsById(id)) {
                throw new AppException(NOT_FOUND_USER);
            }
        });

        return chatReportRepository.getChatReportCount(userIds);
    }
}
