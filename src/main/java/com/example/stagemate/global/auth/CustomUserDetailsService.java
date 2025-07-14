package com.example.stagemate.global.auth;


import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.stagemate.global.exception.CommonErrorCode.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(NOT_FOUND));

        User user = entity.toDomain();

        return new CustomUserDetails(user,null);
    }

    public CustomUserDetails loadUserById(Long userId) {
        UserJpaEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NOT_FOUND));

        User user = entity.toDomain();

        return new CustomUserDetails(user, null);
    }
}