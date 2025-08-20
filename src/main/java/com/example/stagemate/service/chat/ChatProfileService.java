package com.example.stagemate.service.chat;

import com.example.stagemate.dto.response.AccountInfoResponse;
import com.example.stagemate.dto.response.UserBlockStatusResponse;
import com.example.stagemate.dto.response.chat.ChatProfileResponse;
import com.example.stagemate.dto.response.chat.ChatReportCountResponse;
import com.example.stagemate.service.community.UserBlockService;
import com.example.stagemate.service.report.ReportService;
import com.example.stagemate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatProfileService {
    private final UserService userService;
    private final ReportService reportService;
    private final UserBlockService userBlockService;

    public List<ChatProfileResponse> getChatProfile(Long userId, List<Long> senderIds) {
        // 프로필 정보
        List<AccountInfoResponse> accountInfos = userService.getAccountInfo(senderIds);

        // 신고 횟수
        List<ChatReportCountResponse> chatReportCounts = reportService.getChatReportCount(senderIds);

        // 차단 여부
        List<UserBlockStatusResponse> isBlocked = userBlockService.checkBlockedUser(userId, senderIds);

        // 최종 빌드
        return buildChatProfiles(accountInfos, chatReportCounts, isBlocked);
    }

    private List<ChatProfileResponse> buildChatProfiles(
            List<AccountInfoResponse> accountInfos,
            List<ChatReportCountResponse> reportCounts,
            List<UserBlockStatusResponse> blockStatuses
    ) {
        // 신고 수, 차단 여부를 userId 기준으로 Map에 담기
        Map<Long, Integer> reportCountMap = reportCounts.stream()
                .collect(Collectors.toMap(ChatReportCountResponse::userId,
                        r -> r.reportCount().intValue()));

        Map<Long, Boolean> blockMap = blockStatuses.stream()
                .collect(Collectors.toMap(UserBlockStatusResponse::userId,
                        UserBlockStatusResponse::isBlocked));

        // AccountInfoResponse → ChatProfileResponse 변환
        return accountInfos.stream()
                .map(user -> ChatProfileResponse.from(
                        user.getId(),                          // senderId
                        user.getProfileImageUrl(),             // 프로필 이미지
                        user.getNickname(),                    // 닉네임
                        reportCountMap.getOrDefault(user.getId(), 0),   // 신고 횟수
                        blockMap.getOrDefault(user.getId(), false)      // 차단 여부
                ))
                .toList();
    }
}
