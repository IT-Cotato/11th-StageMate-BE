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
    private String content;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private MagazineCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MagazineImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MagazineLike> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MagazineScrap> scraps = new ArrayList<>();
}
