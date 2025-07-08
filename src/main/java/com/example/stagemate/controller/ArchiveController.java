package com.example.stagemate.controller;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.archive.ArchiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ArchiveController {
    private final ArchiveService archiveService;

    @Tag(name = "Archive", description = "Archive API")
    @Operation(summary = "아카이브 상세 정보", description = "아카이브 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "아카이브 상세 정보를 가져옴")
    @GetMapping("/api/v1/archive/{archiveId}")
    public DataResponse<Archive> getArchive(@PathVariable Long archiveId) {
        return DataResponse.from(archiveService.getArchive(archiveId));
    }

    @Tag(name = "Archive", description = "Archive API")
    @Operation(summary = "아카이브 생성", description = "아카이브 생성")
    @ApiResponse(responseCode = "200", description = "아카이브 생성")
    @PostMapping("/api/v1/archive")
    public DataResponse<Long> createArchive(@Valid @RequestBody ArchiveCreateRequest archiveCreateRequest) {
        return DataResponse.from(archiveService.createArchive(Archive.create(archiveCreateRequest)));
    }

    @Tag(name = "Archive", description = "Archive API")
    @Operation(summary = "아카이브 삭제", description = "아카이브 삭제")
    @ApiResponse(responseCode = "200", description = "아카이브 삭제")
    @DeleteMapping("/api/v1/archive/{archiveId}")
    public DataResponse<Void> deleteArchive(@PathVariable Long archiveId) {
        archiveService.deleteArchive(archiveId);
        return DataResponse.ok();
    }

//    archive Update Reqeust 필요
    @Tag(name = "Archive", description = "Archive API")
    @Operation(summary = "아카이브 변경", description = "아카이브 변경")
    @ApiResponse(responseCode = "200", description = "아카이브 변경")
    @PutMapping("/api/v1/archive/{archiveId}")
    public DataResponse<?> updateArchive(@PathVariable Long archiveId, @Valid @RequestBody ArchiveUpdateRequest archiveUpdateRequest) {
        archiveService.updateArchive(archiveId, archiveUpdateRequest);
        return DataResponse.ok();
    }

}
