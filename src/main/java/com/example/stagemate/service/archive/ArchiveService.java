package com.example.stagemate.service.archive;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.archive.ArchiveErrorCode;
import com.example.stagemate.repository.ArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ArchiveService {
    private final ArchiveRepository archiveRepository;

    public Archive getArchive(Long archiveId) {
        return archiveRepository.findById(archiveId)
                .orElseThrow(() -> new AppException(ArchiveErrorCode.NOT_FOUND));
    }

    public List<Archive> getArchives(UserJpaEntity user, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);


        //유저 월별 공연 아카이브 목록을 가져오는 쿼리
        return archiveRepository.findByUserAndViewingDateBetween(user, startDate, endDate);
    }

    public Long createArchive(ArchiveCreateRequest archiveCreateRequest, UserJpaEntity user) {
        Archive archive = Archive.create(archiveCreateRequest, user);

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
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new AppException(ArchiveErrorCode.NOT_FOUND));

        //유저 권한 검증
        archive.validateDeleteOrUpdateBy(user);

        archive.update(archiveUpdateRequest);
    }


}
