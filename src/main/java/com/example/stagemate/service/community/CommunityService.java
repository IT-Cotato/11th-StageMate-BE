package com.example.stagemate.service.community;

import com.example.stagemate.domain.community.*;
import com.example.stagemate.domain.image.Image;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityPostCreateRequest;
import com.example.stagemate.dto.request.community.CommunityPostUpdateRequest;
import com.example.stagemate.dto.response.community.*;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.ImageRepository;
import com.example.stagemate.repository.community.CommunityImageRepository;
import com.example.stagemate.repository.community.CommunityLikeRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.community.CommunityScrapRepository;
import com.example.stagemate.repository.user.UserJpaRepository;
import com.example.stagemate.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.stagemate.global.exception.CommonErrorCode.NOT_FOUND_USER;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_POST_NOT_AUTHOR;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserJpaRepository userRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityScrapRepository communityScrapRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommunityScrapService communityScrapService;

    // 커뮤니티 게시글 작성, 이미지 업로드
    public CommunityPostResponse createCommunityPost(UserJpaEntity user, CommunityPostCreateRequest request, List<MultipartFile> images) {
        userRepository.findById(user.getId()).orElseThrow(() -> new AppException(NOT_FOUND_USER));
        CommunityCategory communityCategory = CommunityCategory.from(request.getCategory());
        TradeCategory tradeCategory = communityCategory == CommunityCategory.TRADE
                ? TradeCategory.from(request.getTradeCategory())
                : null; // 나눔거래가 아닐 경우 null 처리

        TradeMethod tradeMethod = communityCategory == CommunityCategory.TRADE
                ? TradeMethod.from(request.getTradeMethod())
                : null; // 나눔거래가 아닐 경우 null 처리
        CommunityPost post = request.toEntity(user, communityCategory, tradeCategory, tradeMethod);
        communityRepository.save(post);

        // 이미지가 있다면 업로드 및 연결
        if (images != null && !images.isEmpty()) {
            int order = 1;
            for (MultipartFile image : images) {
                Image uploadImage = imageService.uploadImage(image);
                // 중간 테이블 연결, 순서 포함
                CommunityImage communityImage = CommunityImage.builder()
                        .communityPost(post)
                        .image(uploadImage)
                        .sortOrder(order++)
                        .build();
                communityImageRepository.save(communityImage);

                post.getImages().add(communityImage);
            }
        }

        // 게시글 작성 시점 사용자는 본인의 스크랩과 좋아요를 누르지 않음
        return CommunityPostResponse.from(post, false, false);
    }

    // 커뮤니티 게시글 목록 조회(페이징)
    // HOT
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
//    public CommunityPostPagedResponse getCommunityHotPosts(UserJpaEntity user, int page, int size) {
//    }


    // 커뮤니티 게시글 목록 조회(페이징)
    // 일상, 꿀팁
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    public CommunityPostPagedResponse getCommunityPosts(UserJpaEntity user, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        // 비회원은 전체공개 글만 조회 가능
        if (user == null) {
            Page<CommunityPost> communityPosts = communityRepository.findAllByDeletedFalseAndCategoryAndMembersOnlyFalseOrderByCreatedAtDesc(
                    CommunityCategory.from(category), pageable
            );
            List<CommunityPostListResponse> list = communityPosts.stream()
                    .map(CommunityPostListResponse::from)
                    .toList();

            return new CommunityPostPagedResponse(
                    list,
                    communityPosts.getNumber(),
                    communityPosts.getSize(),
                    communityPosts.getTotalElements(),
                    communityPosts.getTotalPages()
            );
        } else {
            // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
            // 차단 로직은 추후 추가
            Page<CommunityPost> communityPosts = communityRepository.findAllByDeletedFalseAndCategoryOrderByCreatedAtDesc(
                    CommunityCategory.from(category), pageable
            );
            List<CommunityPostListResponse> list = communityPosts.stream()
                    .map(CommunityPostListResponse::from)
                    .toList();
            return new CommunityPostPagedResponse(
                    list,
                    communityPosts.getNumber(),
                    communityPosts.getSize(),
                    communityPosts.getTotalElements(),
                    communityPosts.getTotalPages()
            );
        }
    }

    // 커뮤니티 게시글 목록 조회(페이징)
    // 나눔거래
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    public CommunityPostTradePagedResponse getCommunityTradePosts(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        // 비회원은 전체공개 글만 조회 가능
        if (user == null) {
            Page<CommunityPost> communityPosts = communityRepository.findAllByDeletedFalseAndCategoryAndMembersOnlyFalseOrderByCreatedAtDesc(
                    CommunityCategory.TRADE, pageable
            );
            boolean isScrapped = false;
            List<CommunityPostTradeListResponse> list = communityPosts.stream()
                    .map(post -> CommunityPostTradeListResponse.from(post, isScrapped))
                    .toList();

            return new CommunityPostTradePagedResponse(
                    list,
                    communityPosts.getNumber(),
                    communityPosts.getSize(),
                    communityPosts.getTotalElements(),
                    communityPosts.getTotalPages()
            );
        } else {
            // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
            // 차단 로직은 추후 추가
            Page<CommunityPost> communityPosts = communityRepository.findAllByDeletedFalseAndCategoryOrderByCreatedAtDesc(
                    CommunityCategory.TRADE, pageable
            );
            List<Long> scrappedPostIdsByUser = communityScrapService.getScrappedPostIdsByUser(user.getId());
            List<CommunityPostTradeListResponse> list = communityPosts.stream()
                    .map(post -> CommunityPostTradeListResponse.from(post, scrappedPostIdsByUser.contains(post.getId())))
                    .toList();
            return new CommunityPostTradePagedResponse(
                    list,
                    communityPosts.getNumber(),
                    communityPosts.getSize(),
                    communityPosts.getTotalElements(),
                    communityPosts.getTotalPages()
            );
        }
    }


    // 커뮤니티 게시글 상세 조회
    // 일상, 꿀팁, 나눔거래 (HOT 포함)
    // viewCount++;
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    // 차단 로직 추후 추가
    public CommunityPostResponse getCommunityPostDetail(Long postId, UserJpaEntity user) {
        CommunityPost post = getCommunityPost(postId);

        // 비회원은 전체공개 글만 조회 가능
        if (user == null && post.isMembersOnly()) {
            throw new AppException(NOT_FOUND_USER);
        }

        // 게시글 조회수 증가
        post.changeViewCount();

        // 스크랩 여부 확인
        boolean isScrapped = false;
        if (user != null) {
            isScrapped = communityScrapRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);
        }

        // 좋아요 여부 확인
        boolean isLiked = false;
        if (user != null) {
            isLiked = communityLikeRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);
        }

        return CommunityPostResponse.from(post, isScrapped, isLiked);

    }


    public void toggleCommunityPostLike(Long postId, UserJpaEntity user) {
        CommunityPost post = getCommunityPost(postId);

        // 유저 존재하는지 확인
        if (user == null) {
            throw new AppException(NOT_FOUND_USER);
        }

        if(communityLikeRepository.existsByUserIdAndCommunityPostId(user.getId(), postId)) {
            // 이미 좋아요를 누른 경우, 좋아요 취소
            communityLikeRepository.deleteByUserIdAndCommunityPostId(user.getId(), postId);
            post.decrementLikeCount();
        } else {
            // 좋아요 추가
            communityLikeRepository.save(CommunityLike.of(user, post));
            post.incrementLikeCount();
        }
    }


    public void toggleCommunityPostScrap(Long postId, UserJpaEntity user) {
        CommunityPost post = getCommunityPost(postId);

        // 유저 존재하는지 확인
        if (user == null) {
            throw new AppException(NOT_FOUND_USER);
        }

        if (communityScrapRepository.existsByUserIdAndCommunityPostId(user.getId(), postId)) {
            // 이미 스크랩한 경우, 스크랩 취소
            communityScrapRepository.deleteByUserIdAndCommunityPostId(user.getId(), postId);
            post.decrementScrapCount();
        } else {
            // 스크랩 추가
            communityScrapRepository.save(CommunityScrap.of(user, post));
            post.incrementScrapCount();
        }
    }


    public CommunityPostResponse updateCommunityPost(UserJpaEntity user, Long postId, CommunityPostUpdateRequest request, List<MultipartFile> newImages) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));

        // 게시글 작성자와 요청한 사용자가 일치하는지 확인
        if (post.getAuthor() != user) {
            throw new AppException(COMMUNITY_POST_NOT_AUTHOR);
        }

        // 게시글 정보 업데이트
        post.update(request);

        // 이미지 업데이트
        updateCommunityPostImage(post, request.getImageIds(), newImages);

        // 스크랩, 좋아요 여부 확인
        boolean isScrapped = communityScrapRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);
        boolean isLiked = communityLikeRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);

        return CommunityPostResponse.from(post, isScrapped, isLiked);
    }


    // 기존 올렸던 이미지 순서를 유지하면서 새로운 이미지 추가
    private void updateCommunityPostImage(CommunityPost post, List<Long> remainImageIds, List<MultipartFile> newImages) {
        List<CommunityImage> existingImages = new ArrayList<>(post.getImages()); // 기존 이미지 복사

        post.getImages().clear(); // 게시글의 이미지 리스트 초기화

        // 삭제할 이미지 제거
        for (CommunityImage image : existingImages) {
            if (remainImageIds == null || !remainImageIds.contains(image.getId())) {
                communityImageRepository.delete(image);
                imageRepository.delete(image.getImage());
            }
        }

        // 유지할 이미지 순서대로 다시 추가
        List<CommunityImage> remainImages = existingImages.stream()
                .filter(img -> remainImageIds != null && remainImageIds.contains(img.getId()))
                .sorted(Comparator.comparingInt(CommunityImage::getSortOrder))
                .toList();

        // 순서 재설정
        int order = 1;
        for (CommunityImage img : remainImages) {
            img.setSortOrder(order++);
        }

        post.getImages().addAll(remainImages);

        // 새로 추가된 이미지들 처리
        order = remainImages.size() + 1;
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                Image uploadImage = imageService.uploadImage(image);

                CommunityImage newCommunityImage = CommunityImage.builder()
                        .communityPost(post)
                        .image(uploadImage)
                        .sortOrder(order++)
                        .build();

                communityImageRepository.save(newCommunityImage);
                post.getImages().add(newCommunityImage);
            }
        }
    }


    public void deleteCommunityPost(Long postId, UserJpaEntity user) {
        CommunityPost post = getCommunityPost(postId);
        // 게시글 작성자와 요청한 사용자가 일치하는지 확인
        if (post.getAuthor() != user) {
            throw new AppException(COMMUNITY_POST_NOT_AUTHOR);
        }

        // soft delete 처리
        post.changeIsDeleted();
    }

    // 게시글 작성자와 요청한 사용자가 일치하는지 확인
    private CommunityPost getCommunityPost(Long postId) {
        return communityRepository.findById(postId)
                .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));
    }

}
