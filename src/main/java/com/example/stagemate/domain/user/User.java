package com.example.stagemate.domain.user;

import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.domain.user.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private final Long id;
    private final String name;
    private final String nickname;
    private final String email;
    private final String password;
    private final String userId;
    private final String picture;
    private final LocalDate birthdate;
    private final LoginType loginType;
    private final Role role;
    private Map<ConsentType, Boolean> consents;

    public static User normalSignUp(String userId, String email, String password, String name, String nickname, LocalDate birthdate, Map<ConsentType, Boolean> consents) {
        return new User(null, name, nickname, email, password, userId, null, birthdate, LoginType.NORMAL, Role.USER, consents);
    }

    public static User googleSignUp(String email, String userId, String picture, String name, String nickname, LocalDate birthdate, Map<ConsentType, Boolean> consents) {
        return new User(null, name, nickname, email, null, userId, picture, birthdate, LoginType.GOOGLE, Role.USER, consents);
    }

    public static User normalGuestSignUp(String userId, String email, String password, String name, String nickname, LocalDate birthdate) {
        return User.builder()
                .userId(userId)
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .birthdate(birthdate)
                .loginType(LoginType.NORMAL)
                .role(Role.GUEST)
                .build();
    }

    public static User googleGuestSignUp(String userId, String name, String email, String picture) {
        return User.builder()
                .userId(userId)
                .name(name)
                .email(email)
                .picture(picture)
                .loginType(LoginType.GOOGLE)
                .role(Role.GUEST)
                .consents(Map.of()) // No consents for guest
                .build();
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public User update(String name, String picture) {
        return this.toBuilder()
                .name(name)
                .picture(picture)
                .build();
    }

    public User updateGuestInfo(String nickname, LocalDate birthdate) {
        return this.toBuilder()
                .nickname(nickname)
                .birthdate(birthdate)
                .build();
    }

    public User register(Map<ConsentType, Boolean> consents) {
        return this.toBuilder()
                .role(Role.USER)
                .consents(consents)
                .build();
    }
}