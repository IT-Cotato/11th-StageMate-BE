package com.example.stagemate.service.theater;

import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.dto.response.TheaterDetailResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterRepository theaterRepository;

    public PagedResponse<TheaterDetailResponse> getAllTheaters(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Theater> theaters = theaterRepository.findAll(pageable);
        return PagedResponse.from(
                theaters.getContent().stream().map(TheaterDetailResponse::from).toList(),
                theaters);
    }

    public PagedResponse<TheaterDetailResponse> getTheatersByRegion(int page, int size, String region) {
        Pageable pageable = PageRequest.of(page -1, size);

        Page<Theater> theaters = theaterRepository.findByRegion(region, pageable);

        return PagedResponse.from(
                theaters.getContent().stream().map(TheaterDetailResponse::from).toList(),
                theaters);
    }
}
