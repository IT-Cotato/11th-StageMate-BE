package com.example.stagemate.domain.magazine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "magazines")
public class Magazine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_id")
    private Long id;
    private String title;
    private String subTitle;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private MagazineCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MagazineImage> images = new ArrayList<>();

    @Builder.Default
    private int likeCount = 0;
    @Builder.Default
    private int scrapCount = 0;

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }

    public void incrementScrapCount() {
        this.scrapCount++;
    }

}
