package com.example.stagemate.global.auth.dto;

import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.domain.user.type.LoginType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String userId;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey,
                           String userId,
                           String email,
                           String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.userId = userId;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null) {
            System.out.println("[OAuth] email not found in attributes. Falling back to sub.");
            email = (String) attributes.get("sub"); // fallback
        }

        return OAuthAttributes.builder()
                .userId((String) attributes.get("name"))
                .email(email)
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public UserJpaEntity toEntity() {
        return UserJpaEntity.builder()
                .userId(userId)
                .email(email)
                .picture(picture)
                .loginType(LoginType.GOOGLE)
                .role(Role.USER)
                .build();
    }
}
