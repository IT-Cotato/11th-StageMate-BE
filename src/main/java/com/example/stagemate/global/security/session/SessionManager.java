package com.example.stagemate.global.security.session;

import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private static final String EMAIL = "email";
    private static final String SESSION_USER_ID = "SESSION_USER_ID";
    private static final String GUEST_INFO = "GUEST_INFO";

    private final HttpSession session;
    private final HttpServletRequest request;

    public void login(String email) {
        HttpSession session = request.getSession();
        session.setAttribute(EMAIL, email);
    }

    public void logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public String getSessionUserId() {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(SESSION_USER_ID);
    }

    public void setGuestInfo(GuestInfo guestInfo) {
        HttpSession session = request.getSession(true);
        session.setAttribute(GUEST_INFO, guestInfo);
    }

    public GuestInfo getGuestInfo() {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED, "세션이 존재하지 않습니다.");
        }
        Object guestInfo = session.getAttribute(GUEST_INFO);
        if (!(guestInfo instanceof GuestInfo)) {
            throw new AppException(CommonErrorCode.UNAUTHORIZED, "세션에 게스트 정보가 없습니다.");
        }
        return (GuestInfo) guestInfo;
    }
}