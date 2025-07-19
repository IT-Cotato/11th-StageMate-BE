package com.example.stagemate.service.theater;

import com.example.stagemate.domain.theater.Theater;
import com.example.stagemate.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterRepository theaterRepository;

    public List<Theater> getAllTheaters() { return theaterRepository.findAll(); }

    public List<Theater> getTheatersByRegion(String region) { return theaterRepository.findByRegion(region); }
}
