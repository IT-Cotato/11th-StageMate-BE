package com.example.stagemate.controller.auth;

import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/check")
@RequiredArgsConstructor
public class UserCheckController {

    private final UserService userService;

    @GetMapping("/userId/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId, HttpServletRequest request) {
        if (userService.checkUserIdExists(userId)) {
            throw new AppException(AuthErrorCode.DUPLICATE_USER_ID);
        }
        // 세션에 저장
        request.getSession().setAttribute("verified_userId", userId);
        return ResponseEntity.ok(Map.of("isAvailable", true));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@PathVariable String nickname, HttpServletRequest request) {
        if (userService.checkNicknameExists(nickname)) {
            throw new AppException(AuthErrorCode.DUPLICATE_NICKNAME);
        }
        // 세션에 저장
        request.getSession().setAttribute("verified_nickname", nickname);
        return ResponseEntity.ok(Map.of("isAvailable", true));
    }
}
