package com.example.stagemate.domain.community;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityPostUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
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
@Table(name = "community_posts")
public class CommunityPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private CommunityCategory category;
    @Enumerated(EnumType.STRING)
    private TradeCategory tradeCategory; // 나눔거래일때 카테고리(연극, 뮤지컬)
    @Enumerated(EnumType.STRING)
    private TradeMethod tradeMethod; // 나눔거래일떄 방법(추첨나눔/판매/선착나눔)
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserJpaEntity author;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public boolean membersOnly;
    @Column(name = "is_deleted")
    @Builder.Default
    private boolean deleted = false;
    @Builder.Default
    private int viewCount = 0;
    @Builder.Default
    private int commentCount = 0;
    @Builder.Default
    private int likeCount = 0;
    @Builder.Default
    private int scrapCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "communityPost", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CommunityImage> images = new ArrayList<>();


    public void changeViewCount() {
        this.viewCount++;
    }

    public void changeCommentCount() {
        this.commentCount++;
    }

    public void changeIsDeleted() {
        this.deleted = true;
    }

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


    // 나눔거래에서 다른 카테고리로 변경할 경우 고려
    public void update(CommunityPostUpdateRequest request, String jsonContent) {
        CommunityCategory communityCategory = CommunityCategory.from(request.getCategory());
        this.title = request.getTitle();
        this.content = jsonContent;
        this.category = communityCategory;
        this.tradeCategory = communityCategory==CommunityCategory.TRADE ? TradeCategory.from(request.getTradeCategory()) : null;
        this.tradeMethod = communityCategory==CommunityCategory.TRADE ? TradeMethod.from(request.getTradeMethod()) : null;
        this.membersOnly = request.isMembersOnly();
    }
}
