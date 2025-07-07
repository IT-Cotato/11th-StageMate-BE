package com.example.stagemate.service.archive;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.archive.ArchiveErrorCode;
import com.example.stagemate.repository.ArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArchiveService {
    private final ArchiveRepository archiveRepository;

    public Archive getArchive(Long archiveId) {
        return archiveRepository.findById(archiveId)
                .orElseThrow(() -> new AppException(ArchiveErrorCode.NOT_FOUND));
    }

    public Long createArchive(Archive archive) {
        return archiveRepository.save(archive).getId();
    }


    public void deleteArchive(Long archiveId) {
        archiveRepository.deleteById(archiveId);
    }

    public void updateArchive(Archive archive) {
        archiveRepository.save(archive);
    }

}
