package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.global.auth.JwtTokenProvider;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.UserService;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AgreeController {

    private final UserService userService;
    private final LoginUseCase loginUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/agree")
    public ResponseEntity<DataResponse<String>> oAuthAgree(
            @RequestBody @Valid ConsentRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "userId는 필수입니다.");
        }

        // 동의 처리 및 유저 최종 등록
        User completedUser = userService.normalAgreeAndRegister(
                new NormalAgreeCommand(request.getConsents()),
                request.getUserId()
        );

        // 최종 Role 업데이트 후 JWT 발급
        String token = jwtTokenProvider.createToken(completedUser.getId());

        return ResponseEntity.ok(DataResponse.from(token));
    }

}
