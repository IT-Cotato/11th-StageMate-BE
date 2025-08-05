package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.NotificationResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "알림함 조회", description = "내 게시글의 댓글, 내 댓글의 답글에 대한 알림함을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "알림함 조회 성공")
    @GetMapping
    public ResponseEntity<DataResponse<List<NotificationResponse>>> getNotifications(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        List<NotificationResponse> response = notificationService.getMyNotifications(user);
        return ResponseEntity.ok(DataResponse.from(response));
    }
}
