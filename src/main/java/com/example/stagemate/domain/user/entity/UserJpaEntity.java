package com.example.stagemate.domain.user.entity;

import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.User;

import com.example.stagemate.domain.user.model.UserConsent;
import com.example.stagemate.domain.user.type.LoginType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "picture")
    private String picture;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserConsent> consents = new ArrayList<>();

    public UserJpaEntity update(String userId, String picture) {
        this.userId = userId;
        this.picture = picture;

        return this;
    }

    public User toDomain() {
        return User.builder()
                .id(id)
                .userId(userId)
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .picture(picture)
                .birthdate(birthdate)
                .loginType(loginType)
                .role(role)
                .consents(consents.stream().map(UserConsent::getConsentType).collect(Collectors.toList()))
                .build();
    }

    public static UserJpaEntity from(User user) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .nickname(user.getNickname())
                .picture(user.getPicture())
                .birthdate(user.getBirthdate())
                .loginType(user.getLoginType())
                .role(user.getRole())
                .build();

        if (user.getConsents() != null) {
            List<UserConsent> userConsents = user.getConsents().stream()
                    .map(consentType -> UserConsent.builder()
                            .consentType(consentType)
                            .agreed(true)
                            .build())
                    .collect(Collectors.toList());
            entity.setConsents(userConsents);
        }

        return entity;
    }

    private void setConsents(List<UserConsent> consents) {
        this.consents.clear();
        if (consents != null) {
            this.consents.addAll(consents);
            consents.forEach(consent -> consent.setUser(this));
        }
    }
}
