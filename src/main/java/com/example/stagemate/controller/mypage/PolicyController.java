package com.example.stagemate.controller.mypage;

import com.example.stagemate.dto.response.PolicyResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.mypage.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage/policy")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/terms")
    public ResponseEntity<DataResponse<PolicyResponse>> getTerms() {
        PolicyResponse response = policyService.getTerms();
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @GetMapping("/privacy")
    public ResponseEntity<DataResponse<PolicyResponse>> getPrivacyPolicy() {
        PolicyResponse response = policyService.getPrivacyPolicy();
        return ResponseEntity.ok(DataResponse.from(response));
    }
}

