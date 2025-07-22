package com.example.stagemate.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class EmailVerificationRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "email:code:";
    private static final String VERIFIED_PREFIX = "email:verified:";

    //인증번호 저장
    public void saveCode(String email, String code, Duration ttl) {
        redisTemplate.opsForValue().set(PREFIX + email, code, ttl);
    }

    //인증번호 검증 + 인증 완료 처리
    public boolean verifyCode(String email, String code) {
        String stored = redisTemplate.opsForValue().get(PREFIX + email);
        boolean success = code.equals(stored);

        if (success) {
            // 인증 완료 처리
            redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", Duration.ofMinutes(5));
        }
        return success;
    }

    // 이메일 인증 완료 여부 확인
    public boolean isVerified(String email) {
        String value = redisTemplate.opsForValue().get(VERIFIED_PREFIX + email);
        return "true".equals(value);
    }


}