package com.example.stagemate.dto.response;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AccountInfoResponse {
    private String userId;
    private String email;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String profileImageUrl;

    public AccountInfoResponse(String userId, String email, String name,
                               String nickname, LocalDate birth, String profileImageUrl) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.profileImageUrl = profileImageUrl;
    }

    public static AccountInfoResponse from(UserJpaEntity user) {
        return new AccountInfoResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getBirthdate(),
                user.getProfileImageUrl()
        );
    }
}

