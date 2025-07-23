package com.example.stagemate.repository.user;

import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}