package com.example.stagemate.domain.magazine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "magazine_statistics")
public class MagazineStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_statistics_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    int likeCount;
    int scrapCount;
    int totalCount;
    LocalDateTime updatedAt;

    public static MagazineStatistics of(Magazine magazine) {
        return MagazineStatistics.builder()
                .magazine(magazine)
                .likeCount(magazine.getLikeCount())
                .scrapCount(magazine.getScrapCount())
                .totalCount(magazine.getLikeCount() + magazine.getScrapCount())
                .updatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }
}
