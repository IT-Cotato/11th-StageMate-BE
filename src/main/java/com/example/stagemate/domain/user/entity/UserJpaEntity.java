package com.example.stagemate.domain.user.entity;

import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.User;

import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.domain.user.model.UserConsent;
import com.example.stagemate.domain.user.LoginType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Column(name = "profile_image_url")
    private String profileImageUrl;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserConsent> consents = new ArrayList<>();


    public User toDomain() {
        Map<ConsentType, Boolean> consentMap = consents.stream()
                .collect(Collectors.toMap(
                        UserConsent::getConsentType,
                        UserConsent::isAgreed
                ));

        return User.builder()
                .id(id)
                .userId(userId)
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .picture(picture)
                .birthdate(birthdate)
                .loginType(loginType)
                .role(role)
                .consents(consentMap)
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

        if (user.getConsents() != null && !user.getConsents().isEmpty()) {
            List<UserConsent> userConsents = user.getConsents().entrySet().stream()
                    .map(entry -> UserConsent.builder()
                            .consentType(entry.getKey())
                            .agreed(entry.getValue())
                            .user(entity)
                            .build())
                    .collect(Collectors.toList());
            entity.setConsents(userConsents);
        }

        return entity;
    }

    public void setConsents(List<UserConsent> consents) {
        if (consents == null) {
            return;
        }
        this.consents.clear();
        this.consents.addAll(consents);
        consents.forEach(consent -> consent.setUser(this));
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
