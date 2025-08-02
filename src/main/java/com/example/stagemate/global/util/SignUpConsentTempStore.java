package com.example.stagemate.global.util;

import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SignUpConsentTempStore {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PREFIX_NORMAL = "signup:terms:normal:";
    private static final String PREFIX_OAUTH = "signup:terms:oauth:";

    // ========== 일반 회원가입 ==========
    public void saveForNormal(String uuid, Map<String, Boolean> consents, Duration ttl) {
        save(PREFIX_NORMAL + uuid, consents, ttl);
    }

    public Map<String, Boolean> getForNormal(String uuid) {
        return get(PREFIX_NORMAL + uuid);
    }

    public void deleteForNormal(String uuid) {
        delete(PREFIX_NORMAL + uuid);
    }

    // ========== 소셜 회원가입 ==========
    public void saveForOAuth(String userId, Map<String, Boolean> consents, Duration ttl) {
        save(PREFIX_OAUTH + userId, consents, ttl);
    }

    public Map<String, Boolean> getForOAuth(String userId) {
        return get(PREFIX_OAUTH + userId);
    }

    public void deleteForOAuth(String userId) {
        delete(PREFIX_OAUTH + userId);
    }


    // ========== 내부 공통 로직 ==========
    private void save(String key, Map<String, Boolean> consents, Duration ttl) {
        String json = convertToJson(consents);
        redisTemplate.opsForValue().set(key, json, ttl);
    }

    private Map<String, Boolean> get(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) throw new AppException(AuthErrorCode.TERMS_NOT_AGREED);
        return convertFromJson(json);
    }

    private void delete(String key) {
        redisTemplate.delete(key);
    }

    private String convertToJson(Map<String, Boolean> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    private Map<String, Boolean> convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Boolean>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
}
