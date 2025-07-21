package com.example.stagemate.controller;

import com.example.stagemate.dto.response.TheaterDetailResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.theater.TheaterService;
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


    @GetMapping("/api/v1/theaters")
    public ResponseEntity<DataResponse<List<TheaterDetailResponse>>> getTheaters(
            @RequestParam(name = "region", required = false) String region
    ) {

        if (region != null) {
            List<TheaterDetailResponse> theaterDetailResponses = theaterService.getTheatersByRegion(region);
            return ResponseEntity.ok(DataResponse.from(theaterDetailResponses));
        }

        List<TheaterDetailResponse> theaterDetailResponses = theaterService.getAllTheaters();
        return ResponseEntity.ok(DataResponse.from(theaterDetailResponses));
    }




}
