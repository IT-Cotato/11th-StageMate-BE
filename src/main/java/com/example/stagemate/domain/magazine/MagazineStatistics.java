package com.example.stagemate.domain.magazine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        int likeCount = magazine.getLikes() == null ? 0 : magazine.getLikes().size();
        int scrapCount = magazine.getScraps() == null ? 0 : magazine.getScraps().size();
        return MagazineStatistics.builder()
                .magazine(magazine)
                .likeCount(likeCount)
                .scrapCount(scrapCount)
                .totalCount(likeCount + scrapCount)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
