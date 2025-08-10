package com.example.stagemate.service.mypage;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.CreateInquiryRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryMailService mailService;

    public void handleInquiry(CreateInquiryRequest req, UserJpaEntity user, HttpServletRequest httpReq) {
        // 저장 안 함. 검증 → 메일 발송
        mailService.sendToAdmin(req, user, httpReq);
    }

}
