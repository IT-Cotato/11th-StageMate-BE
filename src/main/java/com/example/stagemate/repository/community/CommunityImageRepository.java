package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    List<CommunityImage> findAllByCommunityPostId(Long postId);
}
