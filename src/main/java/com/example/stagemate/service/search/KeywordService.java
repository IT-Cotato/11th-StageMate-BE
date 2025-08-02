package com.example.stagemate.service.search;

import com.example.stagemate.dto.response.search.PopularKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final RedisTemplate<String, String> redisTemplate;

    // 10분 단위 키 생성
    private String getCurrent10MinKey() {
        LocalDateTime now = LocalDateTime.now();
        int minute = (now.getMinute() / 10) * 10;
        LocalDateTime baseTime = now.withMinute(minute).withSecond(0).withNano(0);
        return "popular:keywords:" + baseTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    // 직전 10분 키 조회
    private String getPrevious10MinKey() {
        LocalDateTime now = LocalDateTime.now().minusMinutes(10);
        int minute = (now.getMinute() / 10) * 10;
        LocalDateTime baseTime = now.withMinute(minute).withSecond(0).withNano(0);
        return "popular:keywords:" + baseTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    // 검색어 기록
    public void record(String keyword) {
        String key = getCurrent10MinKey();
        redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
        redisTemplate.expire(key, Duration.ofMinutes(15));
    }

    // 직전 구간 인기 검색어 조회 ex. 2025-08-01 22:40 기준
    public PopularKeywordResponse getTop10() {
        String key = getPrevious10MinKey();
        Set<String> result = redisTemplate.opsForZSet().reverseRange(key, 0, 9);
        List<String> list = result != null ? new ArrayList<>(result) : new ArrayList<>();
        LocalDateTime time = parseTimeFromKey(key);
        return new PopularKeywordResponse(time, list);
    }

    private LocalDateTime parseTimeFromKey(String key) {
        // 키 형식: popular:keywords:202508071230
        String[] parts = key.split(":");
        String timePart = parts[2]; // "202508071230"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return LocalDateTime.parse(timePart, formatter);
    }
}
