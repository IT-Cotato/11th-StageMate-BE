package com.example.stagemate.repository.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.domain.user.port.out.LoadUserPort;
import com.example.stagemate.domain.user.port.out.SaveUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByUserId(String userId) {
        return userJpaRepository.findByUserId(userId).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userJpaRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserJpaEntity userJpaEntity = UserJpaEntity.from(user);
        return userJpaRepository.save(userJpaEntity).toDomain();
    }

    @Override
    public Optional<User> findWithConsentsById(Long id) {
        return userJpaRepository.findWithConsentsById(id)
                .map(UserJpaEntity::toDomain);
    }
}