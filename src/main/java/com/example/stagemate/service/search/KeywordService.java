package com.example.stagemate.service.search;

import com.example.stagemate.dto.response.search.PopularKeywordResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.stagemate.global.exception.search.SearchErrorCode.ELASTICSEARCH_ERROR;
import static com.example.stagemate.global.exception.search.SearchErrorCode.REDIS_ERROR;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final RestTemplate restTemplate = new RestTemplate();

    // 직전 구간 인기 검색어 조회 ex. 2025-08-01 22:40 기준
    public PopularKeywordResponse getTop10() {
        String url = "http://localhost:8081/api/v1/search/popular";

        ResponseEntity<DataResponse<PopularKeywordResponse>> res =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<DataResponse<PopularKeywordResponse>>() {}
                );

        // 1) 상태 코드 확인
        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ELASTICSEARCH_ERROR); // 프로젝트 공통 예외/코드에 맞게
        }

        return res.getBody() != null ? res.getBody().getData() : null;

    }


}
