package com.example.stagemate.service.mypage;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.CreateInquiryRequest;
import com.example.stagemate.global.auth.mail.CustomMailSender;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InquiryMailService {
    private final CustomMailSender mailSender;

    private static final String ADMIN_EMAIL = "stagemate25@gmail.com";


    public void sendToAdmin(CreateInquiryRequest req, UserJpaEntity user, HttpServletRequest httpReq) {
        String to = ADMIN_EMAIL;
        String subject = "[문의][" + req.category().name() + "] " + req.title();

        String userAgent = nvl(httpReq.getHeader("User-Agent"), "-");
        String ip = nvl(httpReq.getRemoteAddr(), "-");

        // 관리자에게 도착할 HTML
        String html = """
            <h3>신규 문의가 접수되었습니다</h3>
            <p><b>카테고리:</b> %s (%s)</p>
            <p><b>제목:</b> %s</p>
            <p><b>내용:</b><br>%s</p>
            <hr>
            <p><b>작성자:</b> %s (userId=%s, nickname=%s)</p>
            <p><b>작성자 이메일:</b> %s</p>
            """.formatted(
                req.category().name(), esc(req.category().getDisplayName()),
                esc(req.title()),
                nl2br(esc(req.content())),
                esc(nvl(user.getName(), "-")),
                esc(nvl(user.getUserId(), "-")),
                esc(nvl(user.getNickname(), "-")),
                esc(nvl(user.getEmail(), "-"))
        );

        try {
            mailSender.sendHtmlWithReplyTo(to, subject, html, user.getEmail()); // Reply-To = 회원 이메일
        } catch (Exception e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR, "문의 메일 발송 실패");
        }
    }

    private static String esc(String s){ return s==null?"":s.replace("<","&lt;").replace(">","&gt;"); }
    private static String nl2br(String s){ return s.replace("\n","<br>"); }
    private static String nvl(String s, String d){ return s==null?d:s; }
}
