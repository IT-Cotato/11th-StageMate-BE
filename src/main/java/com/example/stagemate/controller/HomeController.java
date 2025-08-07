package com.example.stagemate.controller;

import com.example.stagemate.global.auth.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {



    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        if (user != null) {
            model.addAttribute("userName", user.getName());
        } else {
            model.addAttribute("userName", "게스트");
        }
        return "home";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

}
