package com.example.stagemate.controller;

import com.example.stagemate.dto.response.event.EventResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.community.CommunityService;
import com.example.stagemate.service.event.EventService;
import com.example.stagemate.service.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final PerformanceService performanceService;
    private final CommunityService communityService;

    @GetMapping("/api/v1/event")
    public ResponseEntity<DataResponse<List<EventResponse>>> getEvents() {
        //처리가 되지않은 events 조회
        List<EventResponse> events = eventService.getNotUsedEvents();
        return ResponseEntity.ok(DataResponse.from(events));
    }

    @PostMapping("/api/v1/event")
    public ResponseEntity<DataResponse<Void>> markEventIsUsed(@RequestBody List<Long> eventIds) {
        //사용된 이벤트 isUsed true로 바꾸기
        eventService.markEventIsUsed(eventIds);
        return ResponseEntity.ok(DataResponse.ok());
    }


    @GetMapping("/api/v1/event/all-post")
    public ResponseEntity<DataResponse<List<EventResponse>>> getPerformancesV3() {
        List<EventResponse> events = communityService.getAllPostEventsForElkInit();
        return ResponseEntity.ok(DataResponse.from(events));
    }

    @GetMapping("/api/v1/event/all-performance")
    public ResponseEntity<DataResponse<List<EventResponse>>> getAllEvents() {
        List<EventResponse> events = performanceService.getAllPerformanceEventsForElkInit();
        return ResponseEntity.ok(DataResponse.from(events));
    }
}
