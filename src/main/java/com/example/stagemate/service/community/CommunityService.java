package com.example.stagemate.service.community;

import com.example.stagemate.domain.community.*;
import com.example.stagemate.domain.image.Image;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityPostCreateRequest;
import com.example.stagemate.dto.request.community.CommunityPostUpdateRequest;
import com.example.stagemate.dto.response.community.*;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.ImageRepository;
import com.example.stagemate.repository.community.*;
import com.example.stagemate.repository.user.UserJpaRepository;
import com.example.stagemate.service.image.ImageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.stagemate.global.exception.CommonErrorCode.NOT_FOUND_USER;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {
    private static final Logger log = LoggerFactory.getLogger(CommunityService.class);
    private final CommunityRepository communityRepository;
    private final UserJpaRepository userRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityScrapRepository communityScrapRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommunityScrapService communityScrapService;
    private final CommunityStatisticsRepository communityStatisticsRepository;
    private final CommunityLikeService communityLikeService;
    private final ObjectMapper objectMapper;
    private final CommunityCommentService communityCommentService;
    private final CommunityReportRepository communityReportRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserBlockRepository userBlockRepository;

    // 커뮤니티 게시글 작성, 이미지 업로드
    public CommunityPostResponse createCommunityPost(UserJpaEntity user, CommunityPostCreateRequest request, List<MultipartFile> images) throws JsonProcessingException {
        // json -> 문자열로 변환
        String jsonString = objectMapper.writeValueAsString(request.getContent());
        userRepository.findById(user.getId()).orElseThrow(() -> new AppException(NOT_FOUND_USER));
        CommunityCategory communityCategory = CommunityCategory.from(request.getCategory());
        TradeCategory tradeCategory = communityCategory == CommunityCategory.TRADE
                ? TradeCategory.from(request.getTradeCategory())
                : null; // 나눔거래가 아닐 경우 null 처리

        TradeMethod tradeMethod = communityCategory == CommunityCategory.TRADE
                ? TradeMethod.from(request.getTradeMethod())
                : null; // 나눔거래가 아닐 경우 null 처리
        CommunityPost post = request.toEntity(user, communityCategory, tradeCategory, tradeMethod, jsonString);
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
        // 역직렬화
        JsonNode jsonContent = objectMapper.readTree(post.getContent());

        // 게시글 작성 시점 사용자는 본인의 스크랩과 좋아요를 누르지 않음
        return CommunityPostResponse.from(post, false, false, jsonContent, null);
    }

    // 커뮤니티 게시글 목록 조회(페이징)
    // HOT
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    public CommunityPostPagedResponse getCommunityHotPosts(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<CommunityStatistics> communityStatistics;
        List<CommunityPostListResponse> list;
        // 비회원은 전체공개 글만 조회 가능
        if (user == null) {
            communityStatistics = communityStatisticsRepository.findAllByMembersOnlyFalseOrderByTotalCountDesc(pageable);
            boolean isLiked = false;
            list = communityStatistics.stream()
                    .map(post -> CommunityPostListResponse.fromStat(post, isLiked))
                    .toList();
        } else {
            // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
            communityStatistics = communityStatisticsRepository.findAllByOrderByTotalCountDesc(pageable);
            List<Long> likedPostIdsByUser = communityLikeService.getLikedPostIdsByUser(user.getId());

            Set<Long> blockedUserIds = userBlockRepository.findAllByBlockerId(user.getId())
                    .stream()
                    .map(block -> block.getBlocked().getId())
                    .collect(Collectors.toSet());

            list = communityStatistics.stream()
                    .map(stat -> {
                        boolean isBlocked = blockedUserIds.contains(stat.getCommunityPost().getAuthor().getId());
                        if (isBlocked) {
                            return CommunityPostListResponse.maskedStat(stat, likedPostIdsByUser.contains(stat.getId())); // 마스킹된 응답 생성
                        } else {
                            return CommunityPostListResponse.fromStat(stat, likedPostIdsByUser.contains(stat.getId()));
                        }
                    })
                    .toList();
        }

        return new CommunityPostPagedResponse(
                list,
                communityStatistics.getNumber(),
                communityStatistics.getSize(),
                communityStatistics.getTotalElements(),
                communityStatistics.getTotalPages()
        );
    }


    // 커뮤니티 게시글 목록 조회(페이징)
    // 일상, 꿀팁
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    public CommunityPostPagedResponse getCommunityPosts(UserJpaEntity user, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<CommunityPost> communityPosts;
        List<CommunityPostListResponse> list;
        // 비회원은 전체공개 글만 조회 가능
        if (user == null) {
            communityPosts = communityRepository.findAllByDeletedFalseAndCategoryAndMembersOnlyFalseOrderByCreatedAtDesc(
                    CommunityCategory.from(category), pageable
            );
            boolean isLiked = false;
            list = communityPosts.stream()
                    .map(post -> CommunityPostListResponse.from(post, isLiked))
                    .toList();


        } else {
            // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
            communityPosts = communityRepository.findAllByDeletedFalseAndCategoryOrderByCreatedAtDesc(
                    CommunityCategory.from(category), pageable
            );
            List<Long> likedPostIdsByUser = communityLikeService.getLikedPostIdsByUser(user.getId());
            Set<Long> blockedUserIds = userBlockRepository.findAllByBlockerId(user.getId()).stream()
                    .map(block -> block.getBlocked().getId())
                    .collect(Collectors.toSet());

            list = communityPosts.stream()
                    .map(post -> {
                        boolean isBlocked = blockedUserIds.contains(post.getAuthor().getId());
                        if (isBlocked) {
                            return CommunityPostListResponse.masked(post, likedPostIdsByUser.contains(post.getId()));
                        } else {
                            return CommunityPostListResponse.from(post, likedPostIdsByUser.contains(post.getId()));
                        }
                    })
                    .toList();
        }
        return new CommunityPostPagedResponse(
                list,
                communityPosts.getNumber(),
                communityPosts.getSize(),
                communityPosts.getTotalElements(),
                communityPosts.getTotalPages()
        );
    }

    // 커뮤니티 게시글 목록 조회(페이징)
    // 나눔거래
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    public CommunityPostTradePagedResponse getCommunityTradePosts(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<CommunityPost> communityPosts;
        List<CommunityPostTradeListResponse> list;
        // 비회원은 전체공개 글만 조회 가능
        if (user == null) {
            communityPosts = communityRepository.findAllByDeletedFalseAndCategoryAndMembersOnlyFalseOrderByCreatedAtDesc(
                    CommunityCategory.TRADE, pageable
            );
            boolean isScrapped = false;
            list = communityPosts.stream()
                    .map(post -> CommunityPostTradeListResponse.from(post, isScrapped))
                    .toList();

        } else {
            // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
            communityPosts = communityRepository.findAllByDeletedFalseAndCategoryOrderByCreatedAtDesc(
                    CommunityCategory.TRADE, pageable
            );
            List<Long> scrappedPostIds = communityScrapService.getScrappedPostIdsByUser(user.getId());
            Set<Long> blockedUserIds = userBlockRepository.findAllByBlockerId(user.getId()).stream()
                    .map(block -> block.getBlocked().getId())
                    .collect(Collectors.toSet());
            list = communityPosts.stream()
                    .map(post -> {
                        boolean isBlocked = blockedUserIds.contains(post.getAuthor().getId());
                        if (isBlocked) {
                            return CommunityPostTradeListResponse.masked(post, scrappedPostIds.contains(post.getId()));
                        } else {
                            return CommunityPostTradeListResponse.from(post, scrappedPostIds.contains(post.getId()));
                        }
                    })
                    .toList();
        }
        return new CommunityPostTradePagedResponse(
                list,
                communityPosts.getNumber(),
                communityPosts.getSize(),
                communityPosts.getTotalElements(),
                communityPosts.getTotalPages()
        );
    }


    // 커뮤니티 게시글 상세 조회
    // 일상, 꿀팁, 나눔거래 (HOT 포함)
    // viewCount++;
    // 비회원은 전체공개 글만 조회 가능
    // 회원은 전체공개 + 회원공개 글 중 차단한 사람 제외
    public CommunityPostResponse getCommunityPostDetail(Long postId, UserJpaEntity user) throws JsonProcessingException {
        CommunityPost post = getCommunityPost(postId);

        // 비회원은 전체공개 글만 조회 가능
        if (user == null && post.isMembersOnly()) {
            throw new AppException(MEMBERS_ONLY_POST);
        }

        // 회원일 때 차단한 사람의 게시글
        if (user != null && userBlockRepository.existsByBlockerIdAndBlockedId(user.getId(), post.getAuthor().getId())) {
            throw new AppException(COMMUNITY_BLOCKED_AUTHOR);
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

        // JSON content 파싱
        JsonNode jsonContent = objectMapper.readTree(post.getContent());
        List<CommunityCommentResponse> comments = communityCommentService.getCommentsByPost(post, user);


        return CommunityPostResponse.from(post, isScrapped, isLiked, jsonContent, comments);

    }


    public void toggleCommunityPostLike(Long postId, UserJpaEntity user) {
        CommunityPost post = getCommunityPost(postId);

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


    public CommunityPostResponse updateCommunityPost(UserJpaEntity user, Long postId, CommunityPostUpdateRequest request, List<MultipartFile> newImages) throws JsonProcessingException {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));

        // 게시글 작성자와 요청한 사용자가 일치하는지 확인
        if (!post.getAuthor().getId().equals(user.getId()))
            throw new AppException(COMMUNITY_POST_NOT_AUTHOR);

        // 게시글 정보 업데이트
        post.update(request, objectMapper.writeValueAsString(request.getContent()));

        // 이미지 업데이트
        updateCommunityPostImage(post, request.getImageIds(), newImages);

        // 스크랩, 좋아요 여부 확인
        boolean isScrapped = communityScrapRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);
        boolean isLiked = communityLikeRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);

        // 역직렬화
        JsonNode jsonContent = objectMapper.readTree(post.getContent());

        List<CommunityCommentResponse> comments = communityCommentService.getCommentsByPost(post, user);

        return CommunityPostResponse.from(post, isScrapped, isLiked, jsonContent, comments);
    }


    // 기존 올렸던 이미지 순서를 유지하면서 새로운 이미지 추가
    private void updateCommunityPostImage(CommunityPost post, List<Long> remainImageIds, List<MultipartFile> newImages) {
        // null 방어 및 effectively final 유지
        List<Long> safeImageIdsToRemain = remainImageIds == null ? List.of() : remainImageIds;

        // 기존 이미지 리스트 복사
        List<CommunityImage> originalImages = new ArrayList<>(post.getImages());

        // 삭제할 이미지 필터링 (ID 기준)
        List<CommunityImage> toDelete = originalImages.stream()
                .filter(img -> !safeImageIdsToRemain.contains(img.getImage().getImageId()))
                .toList();

        for (CommunityImage img : toDelete) {
            post.getImages().remove(img);
            img.setCommunityPost(null);
            communityImageRepository.delete(img);
            imageRepository.delete(img.getImage());
        }

        // 남은 이미지 필터링 및 정렬
        List<CommunityImage> remainImages = post.getImages().stream()
                .filter(img -> safeImageIdsToRemain.contains(img.getImage().getImageId()))
                .sorted(Comparator.comparingInt(CommunityImage::getSortOrder))
                .toList();

        // 정렬 순서 재지정
        int order = 1;
        for (CommunityImage img : remainImages) {
            img.setSortOrder(order++);
        }

        // 새 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                Image uploadImage = imageService.uploadImage(image);

                CommunityImage newImage = CommunityImage.builder()
                        .communityPost(post)
                        .image(uploadImage)
                        .sortOrder(order++)
                        .build();
                post.getImages().add(newImage);
                communityImageRepository.save(newImage);
            }
        }
    }



    public void deleteCommunityPost(Long postId, UserJpaEntity user) {
        CommunityPost post = getCommunityPost(postId);
        // 게시글 작성자와 요청한 사용자가 일치하는지 확인
        if (!post.getAuthor().getId().equals(user.getId()))
            throw new AppException(COMMUNITY_POST_NOT_AUTHOR);

        // soft delete 처리
        post.changeIsDeleted();
    }


    private CommunityPost getCommunityPost(Long postId) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));
        if(post.isDeleted())
            throw new AppException(COMMUNITY_POST_NOT_FOUND);
        return post;
    }


    public CommunityPostPagedResponse getMyCommunityScrapList(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        // 유저 존재하는지 확인
        if (user == null) {
            throw new AppException(NOT_FOUND_USER);
        }

        Page<CommunityPost> communityPosts = communityScrapRepository.findScrappedPostsByUser(user.getId(),pageable);
        List<Long> likedPostIdsByUser = communityLikeService.getLikedPostIdsByUser(user.getId());
        List<CommunityPostListResponse> list = communityPosts.stream()
                .map(post -> CommunityPostListResponse.from(post, likedPostIdsByUser.contains(post.getId())))
                .toList();


        return new CommunityPostPagedResponse(
                list,
                communityPosts.getNumber(),
                communityPosts.getSize(),
                communityPosts.getTotalElements(),
                communityPosts.getTotalPages()
        );
    }

    // 내가 작성한 커뮤니티 게시글 목록 조회(페이징)
    public CommunityPostPagedResponse getMyCommunityPosts(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommunityPost> posts = communityRepository.findAllByAuthorIdAndDeletedFalse(user.getId(), pageable);
        List<Long> likedPostIdsByUser = communityLikeService.getLikedPostIdsByUser(user.getId());
        List<CommunityPostListResponse> list = posts.stream()
                .map(post -> CommunityPostListResponse.from(post, likedPostIdsByUser.contains(post.getId())))
                .toList();

        return new CommunityPostPagedResponse(list, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages());
    }

    // 내가 댓글 단 커뮤니티 게시글 목록 조회(페이징)
    public CommunityPostPagedResponse getCommentedCommunityPosts(UserJpaEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CommunityPost> posts = communityCommentRepository.findDistinctPostsByWriterId(user.getId(), pageable);
        List<Long> likedPostIdsByUser = communityLikeService.getLikedPostIdsByUser(user.getId());
        List<CommunityPostListResponse> list = posts.stream()
                .map(post -> CommunityPostListResponse.from(post, likedPostIdsByUser.contains(post.getId())))
                .toList();

        return new CommunityPostPagedResponse(list, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages());
    }




}
