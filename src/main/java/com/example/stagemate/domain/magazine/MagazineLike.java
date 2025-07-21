package com.example.stagemate.domain.magazine;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "magazine_like")
public class MagazineLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @ManyToOne
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    // of 메서드로 객체 생성
    public static MagazineLike of(UserJpaEntity user, Magazine magazine) {
        return MagazineLike.builder()
                .user(user)
                .magazine(magazine)
                .build();
    }
}
