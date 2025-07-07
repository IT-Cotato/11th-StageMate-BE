package com.example.stagemate.controller.user;

import com.example.stagemate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users/check")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/userId/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId) {
        boolean isAvailable = !userService.checkUserIdExists(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@PathVariable String nickname) {
        boolean isAvailable = !userService.checkNicknameExists(nickname);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }
}
