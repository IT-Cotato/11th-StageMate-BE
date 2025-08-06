package com.example.stagemate.controller;

import com.example.stagemate.dto.response.TheaterDetailResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.service.theater.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "공연장", description = "공연장 API")
public class TheaterController {
    private final TheaterService theaterService;


    @Operation(summary = "공연장 목록", description = "공연장 목록을 가져옴, region은 선택")
    @GetMapping("/api/v1/theaters")
    public ResponseEntity<DataResponse<PagedResponse<TheaterDetailResponse>>> getTheaters(
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {

        if (region != null) {
            PagedResponse<TheaterDetailResponse> theaterDetailResponses =
                    theaterService.getTheatersByRegion(page, size, region);

            return ResponseEntity.ok(DataResponse.from(theaterDetailResponses));
        }

        PagedResponse<TheaterDetailResponse> theaterDetailResponses =
                theaterService.getAllTheaters(page, size);

        return ResponseEntity.ok(DataResponse.from(theaterDetailResponses));
    }




}
