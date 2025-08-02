package com.example.stagemate.service.mypage;

import com.example.stagemate.dto.response.PolicyResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PolicyService {

    public PolicyResponse getTerms() {
        String title = "이용약관";
        String content = loadTextFile("terms.txt");
        return new PolicyResponse(title, content);
    }

    public PolicyResponse getPrivacyPolicy() {
        String title = "개인정보 처리방침";
        String content = loadTextFile("privacy.txt");
        return new PolicyResponse(title, content);
    }

    private String loadTextFile(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("policy/" + filename);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("정책 파일을 불러오지 못했습니다.");
        }
    }


}
