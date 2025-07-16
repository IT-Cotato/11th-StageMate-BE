package com.example.stagemate.service.magazine;

import com.example.stagemate.domain.image.Image;
import com.example.stagemate.domain.magazine.*;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.MagazineCreateRequest;
import com.example.stagemate.dto.response.MagazineListResponse;
import com.example.stagemate.dto.response.MagazinePagedResponse;
import com.example.stagemate.dto.response.MagazineResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.magazine.MagazineNotFoundException;
import com.example.stagemate.repository.*;
import com.example.stagemate.repository.user.UserJpaRepository;
import com.example.stagemate.service.image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.stagemate.global.exception.CommonErrorCode.NOT_FOUND_USER;
import static com.example.stagemate.global.exception.magazine.MagazineErrorCode.MAGAZINE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class MagazineService {
    private final MagazineRepository magazineRepository;
    private final ImageService imageService; // 이미지 업로드 서비스
    private final ImageRepository imageRepository;
    private final MagazineImageRepository magazineImageRepository;
    private final MagazineLikeRepository magazineLikeRepository;
    private final MagazineScrapRepository magazineScrapRepository;
    private final MagazineStatisticsRepository magazineStatisticsRepository;
    private final UserJpaRepository userRepository;

    // 매거진 생성(사진 여러개 포함)
    public MagazineResponse createMagazine(MagazineCreateRequest request, List<MultipartFile> images) {
        MagazineCategory magazineCategory = MagazineCategory.from(request.getCategory());
        Magazine entity = request.toEntity(magazineCategory);
        magazineRepository.save(entity);

        if (images != null && !images.isEmpty()) {
            int order = 1;
            // 이미지 저장
            for (MultipartFile image : images) {
                Image uploadImage = imageService.uploadImage(image);

                // 중간 테이블 연결, 순서 포함
                MagazineImage magazineImage = MagazineImage.builder()
                        .magazine(entity)
                        .image(uploadImage)
                        .sortOrder(order++)
                        .build();
                magazineImageRepository.save(magazineImage);

                // 매거진 연관관계 편의 메서드로 이미지 추가
                entity.getImages().add(magazineImage);
            }
        }
        // 매거진 응답 DTO 생성
        return MagazineResponse.from(entity);
    }

    // 최신 순 매거진 보여주기
    public List<MagazineListResponse> getLatestMagazines(int size) {
        List<Magazine> magazines = magazineRepository.findAllByOrderByCreatedAtDesc();
        return magazines.stream()
                .map(MagazineListResponse::from)
                .limit(size)
                .toList();
    }


    // 매거진 목록 조회 (6개씩 페이징)
    public MagazinePagedResponse getMagazineList(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Magazine> magazinePage = magazineRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<MagazineListResponse> content = magazinePage.getContent().stream()
                .map(MagazineListResponse::from)
                .toList();
        return new MagazinePagedResponse(
                content,
                magazinePage.getNumber(),
                magazinePage.getSize(),
                magazinePage.getTotalElements(),
                magazinePage.getTotalPages()
        );
    }

    // 매거진 상세 조회
    public MagazineResponse getMagazineDetail(Long id) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new MagazineNotFoundException(MAGAZINE_NOT_FOUND));

        return MagazineResponse.from(magazine);
    }



    // 매거진 삭제
    public void deleteMagazine(Long id) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new MagazineNotFoundException(MAGAZINE_NOT_FOUND));
        for (MagazineImage mi : magazine.getImages()) {
            imageRepository.delete(mi.getImage());
        }
        magazineRepository.delete(magazine);
    }

    // 매거진 좋아요
    public void likeMagazine(Long magazineId, Long userId) {
        // 매거진 존재하는지 확인
        Magazine magazine = magazineRepository.findById(magazineId).orElseThrow(
                () -> new MagazineNotFoundException(MAGAZINE_NOT_FOUND));
        // 유저 존재하는지 확인
        UserJpaEntity user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(NOT_FOUND_USER));

        if(magazineLikeRepository.existsByUserIdAndMagazineId(user.getId(), magazineId)) {
            // 이미 좋아요를 누른 경
            magazineLikeRepository.deleteByUserIdAndMagazineId(userId, magazineId);
            magazine.getLikes().removeIf(like -> like.getUser().getId().equals(userId));
        } else {
            // 좋아요를 누르지 않은 경우
            MagazineLike magazineLike = magazineLikeRepository.save(MagazineLike.of(user, magazine));
            magazine.getLikes().add(magazineLike);
        }
    }

    // 매거진 스크랩
    public void scrapMagazine(Long magazineId, Long userId) {
        // 매거진 존재하는지 확인
        Magazine magazine = magazineRepository.findById(magazineId).orElseThrow(
                () -> new MagazineNotFoundException(MAGAZINE_NOT_FOUND));
        // 유저 존재하는지 확인
        UserJpaEntity user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(NOT_FOUND_USER));

        if(magazineScrapRepository.existsByUserIdAndMagazineId(userId, magazineId)) {
            // 이미 좋아요를 누른 경우
            magazineScrapRepository.deleteByUserIdAndMagazineId(userId, magazineId);
            magazine.getScraps().removeIf(scrap -> scrap.getUser().getId().equals(userId));
        } else {
            // 좋아요를 누르지 않은 경우
            MagazineScrap magazineScrap = magazineScrapRepository.save(MagazineScrap.of(user, magazine));
            magazine.getScraps().add(magazineScrap);
        }
    }

    // 좋아요 + 스크랩 많은 순 추천 매거진 4개 보여주기
    // 같은 수일 경우, 최신 순으로 정렬
    public List<MagazineListResponse> getRecommendedMagazines() {
        // 매거진 통계 정보 가져오기
        List<MagazineStatistics> statistics = magazineStatisticsRepository.findAll();
        return statistics.stream()
                .map(MagazineStatistics::getMagazine)
                .map(MagazineListResponse::from)
                .toList();

    }
}
