package com.example.stagemate.domain.magazine;

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

    private Long userId; // 일단 userId 저장 -> 추후 User 엔티티와 연관관계 설정 예정

    @ManyToOne
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    // of 메서드로 객체 생성
    public static MagazineLike of(Long userId, Magazine magazine) {
        return MagazineLike.builder()
                .userId(userId)
                .magazine(magazine)
                .build();
    }
}
