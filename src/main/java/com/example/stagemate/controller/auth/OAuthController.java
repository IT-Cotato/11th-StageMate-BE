package com.example.stagemate.controller;

import com.example.stagemate.global.auth.dto.SessionUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "OAuth View")
public class OAuthController {

    private final HttpSession httpSession;

    @GetMapping("/")
    public String home(Model model) {
        // 세션에서 사용자 정보 꺼내기
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userPicture", user.getPicture());
        }
        return "home"; // templates/home.html
    }

}