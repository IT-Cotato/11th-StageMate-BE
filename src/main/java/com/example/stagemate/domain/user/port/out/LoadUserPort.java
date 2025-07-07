package com.example.stagemate.domain.user.port.out;

import com.example.stagemate.domain.user.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
}
