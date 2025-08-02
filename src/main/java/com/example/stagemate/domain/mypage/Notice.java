package com.example.stagemate.domain.mypage;


import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 작성자 정보 (UserJpaEntity와 연관관계 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserJpaEntity author;

    @Column(nullable = false)
    private int viewCount;

    // 생성 시점에 createdAt과 viewCount 초기화
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount += 1;
    }


}
