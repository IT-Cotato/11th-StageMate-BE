package com.example.stagemate.domain.community;

import com.example.stagemate.domain.image.Image;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "community_post_images")
public class CommunityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_image_id")
    private Long id;

    @Setter
    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    private CommunityPost communityPost;

}
