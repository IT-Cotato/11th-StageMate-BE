package com.example.stagemate.repository.user;

import java.util.Optional;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 * Repository for User entity
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByNickname(String nickname);

    @EntityGraph(attributePaths = {"consents"})
    Optional<UserJpaEntity> findWithConsentsById(Long id);
}
