package com.example.stagemate.domain.community;

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
@Table(
        name = "community_statistics",
        indexes = {
                @Index(name = "idx_community_statistics_post_id", columnList = "community_post_id")
        }
)
public class CommunityStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_statistics_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    private CommunityPost communityPost;

    private Long totalCount; // 좋아요 수 + 댓글 수

    private boolean membersOnly; // 회원 전용 여부

}
