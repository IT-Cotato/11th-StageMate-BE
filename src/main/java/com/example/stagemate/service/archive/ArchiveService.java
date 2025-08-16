package com.example.stagemate.service.archive;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.domain.image.Image;
import com.example.stagemate.domain.user.UserErrorCode;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.dto.response.archive.ArchiveDetailResponse;
import com.example.stagemate.dto.response.archive.ArchiveRankingResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.archive.ArchiveErrorCode;
import com.example.stagemate.repository.ArchiveRepository;
import com.example.stagemate.repository.ImageRepository;
import com.example.stagemate.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
@Transactional
public class ArchiveService {
    private final ArchiveRepository archiveRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public ArchiveDetailResponse getArchive(UserJpaEntity user, Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new AppException(ArchiveErrorCode.NOT_FOUND));

        if (!archive.getUser().getId().equals(user.getId())) {
            throw new AppException(UserErrorCode.NO_PERMISSION);
        }

        return ArchiveDetailResponse.from(archive);
    }

    public List<ArchiveDetailResponse> getArchives(UserJpaEntity user, Integer year, Integer month) {

        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);


        //유저 월별 공연 아카이브 목록을 가져오는 쿼리
        List<Archive> archive = archiveRepository.findByUserAndViewingDateBetween(user, startDate, endDate);

        return archive
                .stream()
                .map(ArchiveDetailResponse::from)
                .toList();
    }

    public Long createArchive(UserJpaEntity user, ArchiveCreateRequest archiveCreateRequest, MultipartFile image) {
        validateUserIsNull(user);
        Image uploadImage;

        if (image != null && !image.isEmpty()) {
            //이미지를 직접 올린 경우
            uploadImage = uploadImageAndSave(image);
        } else {
            //이미지를 naver 검색으로 imageUrl로 저장하는 경우
            uploadImage = uploadImageAndSave(archiveCreateRequest.getNaverImageUrl());
        }

        Archive archive = Archive.create(archiveCreateRequest, user, uploadImage);

        return archiveRepository.save(archive).getId();
    }


    public void deleteArchive(UserJpaEntity user, Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new AppException(ArchiveErrorCode.NOT_FOUND));

        //유저 권한 검증
        archive.validateDeleteOrUpdateBy(user);

        archiveRepository.deleteById(archiveId);
    }

    public void updateArchive(UserJpaEntity user, Long archiveId, ArchiveUpdateRequest archiveUpdateRequest) {
        validateUserIsNull(user);

        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new AppException(ArchiveErrorCode.NOT_FOUND));

        //유저 권한 검증
        archive.validateDeleteOrUpdateBy(user);

        //이미지 제외 나머지 속성 변경
        archive.update(archiveUpdateRequest);
    }

    private void validateUserIsNull(UserJpaEntity user) {
        if (user == null) {
            throw new AppException(UserErrorCode.NO_PERMISSION);
        }
    }

    private Image uploadImageAndSave(MultipartFile image) {
        return Optional.ofNullable(image)
                .map(imageService::uploadImage)
                .orElse(null);
    }

    private Image uploadImageAndSave(String imageUrl) {
        return Optional.ofNullable(imageUrl)
                .map(imageService::uploadImage)
                .orElse(null);
    }

    //월별 평점 top
    public List<ArchiveRankingResponse> getTopRatingArchives(int year, int month, int size) {
        Pageable pageable = PageRequest.of(0, size);

        // 월 시작일과 마지막 일 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        Page<Archive> archives = archiveRepository.findTopRatedArchives(startDate, endDate, pageable);

        List<Archive> archiveList = archives.getContent();

        return IntStream.range(0, archiveList.size())
                .mapToObj(i -> ArchiveRankingResponse.from(archiveList.get(i), i + 1))
                .toList();
    }


    //월별 평점 top
    public List<ArchiveRankingResponse> getTopRatingArchivesV2(int year, int month, int size, UserJpaEntity user) {
        Pageable pageable = PageRequest.of(0, size);

        // 월 시작일과 마지막 일 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        Page<Archive> archives = archiveRepository.findTopRatedArchives(startDate, endDate, user.getId(), pageable);

        List<Archive> archiveList = archives.getContent();

        return IntStream.range(0, archiveList.size())
                .mapToObj(i -> ArchiveRankingResponse.from(archiveList.get(i), i + 1))
                .toList();
    }



}
