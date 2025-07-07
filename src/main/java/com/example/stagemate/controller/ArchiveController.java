package com.example.stagemate.controller;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.archive.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ArchiveController {
    private final ArchiveService archiveService;

    @GetMapping("/api/v1/archive/{archiveId}")
    public DataResponse<Archive> getArchive(@PathVariable Long archiveId) {
        return DataResponse.from(archiveService.getArchive(archiveId));
    }

    @PostMapping("/api/v1/archive")
    public DataResponse<Long> createArchive(@RequestBody ArchiveCreateRequest archiveCreateRequest) {
        return DataResponse.from(archiveService.createArchive(Archive.create(archiveCreateRequest)));
    }

    @DeleteMapping("/api/v1/archive/{archiveId}")
    public DataResponse<Void> deleteArchive(@PathVariable Long archiveId) {
        archiveService.deleteArchive(archiveId);
        return DataResponse.ok();
    }

    //archive Update Reqeust 필요
//    @PutMapping("/api/v1/archive/{archiveId}")
//    public DataResponse<?> updateArchive(@PathVariable Long archiveId, Archive archive) {
//        archiveService.updateArchive(archive);
//        return DataResponse.ok();
//    }

}
