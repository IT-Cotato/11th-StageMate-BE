package com.example.stagemate.controller.auth;

import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.global.util.SignUpConsentTempStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/sign-up")
public class AuthAgreeController {

    private final SignUpConsentTempStore consentTempStore;
    private static final Duration TTL = Duration.ofMinutes(10);

    /**
     * 1. 약관 동의 페이지 진입 시: tempUserKey 세션에 저장
     */
    @GetMapping("/tempUserKey")
    public ResponseEntity<DataResponse<String>> createTempKey(HttpServletRequest request) {
        String tempUserKey = UUID.randomUUID().toString();
        request.getSession(true).setAttribute("tempUserKey", tempUserKey);
        return ResponseEntity.ok(DataResponse.from(tempUserKey));
    }

    /**
     * 2. 사용자 약관 동의 완료 시: tempUserKey 기반으로 Redis에 동의 저장
     */
    @PostMapping("/agree")
    public ResponseEntity<DataResponse<Void>> agreeToTermsForNormalUser(
            @RequestBody @Valid ConsentRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("tempUserKey") == null) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "세션이 유효하지 않습니다.");
        }

        Map<String, Boolean> consents = request.getConsents();
        if (!Boolean.TRUE.equals(consents.get("SERVICE_TERMS")) ||
                !Boolean.TRUE.equals(consents.get("PRIVACY_POLICY"))) {
            throw new AppException(AuthErrorCode.TERMS_NOT_AGREED);
        }

        // tempUserKey 기준으로 Redis에 저장
        String tempUserKey = (String) session.getAttribute("tempUserKey");
        consentTempStore.saveForNormal(tempUserKey, consents, TTL);

        return ResponseEntity.ok(DataResponse.ok());

    }

}
