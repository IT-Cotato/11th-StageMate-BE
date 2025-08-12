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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
        log.info("🔍 네이버 이미지 검색 API 호출: query={}, display={}, start={}, sort={}, filter={}", query, display, start, sort, filter);
        validateParams(query, display, start, sort, filter);

        String url = "https://openapi.naver.com/v1/search/image";

        // ✅ 수동 디코딩/인코딩 금지. 원문 그대로 넣고 builder가 한 번만 UTF-8 인코딩
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)      // "바다" 원문 그대로
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .queryParam("filter", filter)
                .encode(StandardCharsets.UTF_8)  // 여기서 딱 한 번 인코딩
                .build()
                .toUri();

        log.info("🔍 네이버 이미지 검색 API 실제 URI(빌더 인코딩 적용): {}", uri);

        try {
            String responseBody = callNaverApi(uri);
            NaverImageSearchResponse parsed = objectMapper.readValue(responseBody, NaverImageSearchResponse.class);
            return ResponseEntity.ok(DataResponse.from(parsed));
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("❌ 네이버 이미지 검색 API 호출 실패", e);
            throw new AppException(NaverImageSearchErrorCode.SE99);
        }
    }

    private String callNaverApi(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // ✅ String URL 대신 URI를 직접 전달 (재인코딩/가공 방지)
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }

    private static String decodeOnce(String s) {
        if (s == null || !s.contains("%")) return s;
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignore) {
            // 잘못된 퍼센트 시퀀스면 원문 유지
            return s;
        }
    }

    private void validateParams(String query, int display, int start, String sort, String filter) {
        if (query == null || query.isEmpty()) {
            throw new AppException(NaverImageSearchErrorCode.SE01);
        }
        if (display < 1 || display > 100) {
            throw new AppException(NaverImageSearchErrorCode.SE02);
        }
        if (start < 1) {
            throw new AppException(NaverImageSearchErrorCode.SE03);
        }
        if (!sort.equals("sim") && !sort.equals("date")) {
            throw new AppException(NaverImageSearchErrorCode.SE04);
        }
        if (!(filter.equals("all") || filter.equals("large") || filter.equals("medium") || filter.equals("small"))) {
            throw new AppException(NaverImageSearchErrorCode.SE01);
        }
    }
}
