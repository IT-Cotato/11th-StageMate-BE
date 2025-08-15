package com.example.stagemate.domain.community;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "community_scrap")
public class CommunityScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private UserJpaEntity user;

    @ManyToOne
    @JoinColumn(name = "community_post_id")
    private CommunityPost communityPost;


    public static CommunityScrap of(UserJpaEntity user, CommunityPost communityPost) {
        return CommunityScrap.builder()
                .user(user)
                .communityPost(communityPost)
                .build();
    }
}
