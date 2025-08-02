package com.example.stagemate.controller.mypage;

import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.NoticeCreateRequest;
import com.example.stagemate.dto.response.NoticeDetailResponse;
import com.example.stagemate.dto.response.NoticeSummaryResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.mypage.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notices")
    @Operation(summary = "공지사항 작성 (운영자 전용)")
    public ResponseEntity<DataResponse<Long>> createNotice(
            @RequestBody @Valid NoticeCreateRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {

        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED);
        }

        Long noticeId = noticeService.createNotice(request, user);
        return ResponseEntity.ok(DataResponse.from(noticeId));
    }

    @GetMapping("notices")
    @Operation(summary = "공지사항 목록 조회")
    public ResponseEntity<DataResponse<Page<NoticeSummaryResponse>>> getNotices(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<NoticeSummaryResponse> result = noticeService.getNotices(pageable);
        return ResponseEntity.ok(DataResponse.from(result));
    }

    @GetMapping("notices/{id}")
    @Operation(summary = "공지사항 상세 조회")
    public ResponseEntity<DataResponse<NoticeDetailResponse>> getNotice(@PathVariable Long id) {
        NoticeDetailResponse result = noticeService.getNoticeDetail(id);
        return ResponseEntity.ok(DataResponse.from(result));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/notices/{id}")
    @Operation(summary = "공지사항 삭제 (운영자 전용)")
    public ResponseEntity<DataResponse<String>> deleteNotice(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        if (user == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED); // 로그인 안 된 사용자
        }

        if (user.getRole() != Role.ADMIN) {
            throw new AppException(AuthErrorCode.NO_ADMIN_PRIVILEGES); // 운영자가 아님
        }
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(DataResponse.from("공지사항이 삭제되었습니다."));

    }
}