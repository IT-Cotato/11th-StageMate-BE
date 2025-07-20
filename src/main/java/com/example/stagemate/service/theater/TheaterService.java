package com.example.stagemate.service.theater;

import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.dto.response.TheaterDetailResponse;
import com.example.stagemate.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterRepository theaterRepository;

    public List<TheaterDetailResponse> getAllTheaters() {
        List<Theater> theaters = theaterRepository.findAll();
        return theaters.stream()
                .map(TheaterDetailResponse::from)
                .collect(Collectors.toList());
    }

    public List<TheaterDetailResponse> getTheatersByRegion(String region) {
        List<Theater> theaters = theaterRepository.findByRegion(region);
        return theaters.stream()
                .map(TheaterDetailResponse::from)
                .collect(Collectors.toList());

    }
}
