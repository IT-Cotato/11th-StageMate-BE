package com.example.stagemate.controller;

import com.example.stagemate.dto.response.naverImage.NaverImageSearchResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.naverImage.NaverImageSearchErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/naver/images")
@Slf4j
public class NaverImageSearchController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ 재사용

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @GetMapping
    public ResponseEntity<DataResponse<NaverImageSearchResponse>> searchImages(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int display,
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "sim") String sort,
            @RequestParam(defaultValue = "all") String filter
    ) {
        String url = "https://openapi.naver.com/v1/search/image";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .queryParam("filter", filter);

        try {
            String responseBody = callNaverApi(builder.toUriString());
            NaverImageSearchResponse parsed = objectMapper.readValue(responseBody, NaverImageSearchResponse.class);
            return ResponseEntity.ok(DataResponse.from(parsed));
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("❌ 네이버 이미지 검색 API 호출 실패", e);
            throw new AppException(NaverImageSearchErrorCode.SE99);
        }
    }

    private String callNaverApi(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            try {
                JsonNode root = objectMapper.readTree(body);
                String errorCode = root.path("errorCode").asText();
                String errorMessage = root.path("errorMessage").asText();

                NaverImageSearchErrorCode code = NaverImageSearchErrorCode.from(errorCode);
                throw new AppException(code, errorMessage); // ✅ message 포함

            } catch (AppException ae) {
                throw ae; // ✅ 다시 던짐
            } catch (JsonProcessingException ex) {
                log.warn("❗ 네이버 API 응답 파싱 실패. 응답 본문: {}", body, ex);
                throw new AppException(NaverImageSearchErrorCode.SE98);
            }
        }
    }
}
