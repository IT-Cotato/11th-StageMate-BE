package com.example.stagemate.controller.auth;

import com.example.stagemate.dto.auth.GuestInfo;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthAgreeController {

    private final SignUpConsentTempStore consentTempStore;
    private static final Duration TTL = Duration.ofMinutes(10);

    @PostMapping("/agree")
    public ResponseEntity<DataResponse<Void>> agreeToTermsForOAuthUser(
            @RequestBody @Valid ConsentRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "세션이 유효하지 않습니다.");
        }

        GuestInfo guestInfo = (GuestInfo) session.getAttribute("guestInfo");
        if (guestInfo == null || guestInfo.userId() == null) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "세션에서 guestInfo 정보를 찾을 수 없습니다.");
        }

        Map<String, Boolean> consents = request.getConsents();
        if (!Boolean.TRUE.equals(consents.get("SERVICE_TERMS")) ||
                !Boolean.TRUE.equals(consents.get("PRIVACY_POLICY"))) {
            throw new AppException(AuthErrorCode.TERMS_NOT_AGREED);
        }

        //이메일에서 생성한 userId 기준으로 Redis에 저장
        consentTempStore.saveForOAuth(guestInfo.userId(), consents, TTL);
        return ResponseEntity.ok(DataResponse.ok());
    }
}
