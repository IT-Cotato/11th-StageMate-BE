package com.example.stagemate.service.search;

import com.example.stagemate.domain.chat.ChatRoom;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.dto.response.search.PerformanceSearchResponse;
import com.example.stagemate.dto.response.search.SearchResultIdsResponse;
import com.example.stagemate.dto.response.search.SearchResultResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.search.SearchErrorCode;
import com.example.stagemate.repository.chat.ChatRoomRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.community.UserBlockRepository;
import com.example.stagemate.repository.performance.PerformanceRepository;
import com.example.stagemate.service.community.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ElasticsearchTemplate template;
    private final CommunityRepository communityRepository;
    private final CommunityLikeService communityLikeService;
    private final UserBlockRepository userBlockRepository;
    private final PerformanceRepository performanceRepository;
    private final KeywordService keywordService;
    private final ChatRoomRepository chatRoomRepository;

    public SearchResultResponse searchAll(String keyword, PerformanceGenre genre, LocalDate date, UserJpaEntity user) {
        // 1) ES에서 ID 묶음 받아오기 (null 가능)
        SearchResultIdsResponse ids = getIdsResponse(keyword, genre, date);

        // 2) null-safe 추출
        List<Long> performanceIds = (ids != null && ids.performanceIds() != null)
                ? ids.performanceIds()
                : List.of();


        List<Long> postIds = (ids != null)
                ? (ids.communityIds() != null ? ids.communityIds() : List.of())
                : List.of();

        // 3) 빈 리스트면 바로 빈 결과, 아니면 조회
        List<PerformanceSearchResponse> performances = performanceIds.isEmpty()
                ? List.of()
                : searchPerformances(performanceIds);

        List<CommunityPostListResponse> posts = postIds.isEmpty()
                ? List.of()
                : searchCommunityPosts(postIds, user);

        // 4) 팩토리 메서드명 맞추기 (record에 정의된 게 of 이면 of 사용)
        return SearchResultResponse.of(performances, posts);
    }

    private SearchResultIdsResponse getIdsResponse(String keyword, PerformanceGenre genre, LocalDate date) {
        // RestTemplate 호출 로직
        try {
            // 1. URL 및 쿼리 파라미터를 안전하게 조립합니다.
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("http://localhost:8081/api/v1/search");
            if (keyword != null && !keyword.isBlank()) {
                builder.queryParam("keyword", keyword);
            }
            if (genre != null) {
                builder.queryParam("performanceGenre", genre.name());
            }
            if (date != null) {
                builder.queryParam("date", date.toString());
            }
            String url = builder.toUriString();

            // 2. 중첩된 제네릭 응답 타입을 정의합니다. (DataResponse<SearchResultResponse>)
            ParameterizedTypeReference<DataResponse<SearchResultIdsResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            // 3. GET 요청을 보내고 ResponseEntity로 전체 응답을 받습니다.
            URI uri = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8081")   // 또는 rootUri 쓰면 fromPath("/api/v1/search")
                    .path("/api/v1/search")
                    .queryParamIfPresent("keyword", Optional.ofNullable(keyword).filter(s -> !s.isBlank()))
                    .queryParamIfPresent("performanceGenre", Optional.ofNullable(genre).map(Enum::name))
                    .queryParamIfPresent("date", Optional.ofNullable(date).map(LocalDate::toString))
                    .build()                                    // ← 여기서는 아직 인코딩 X
                    .encode(StandardCharsets.UTF_8)             // ← ★ 한글 등 안전하게 인코딩
                    .toUri();

            ResponseEntity<DataResponse<SearchResultIdsResponse>> res =
                    restTemplate.exchange(uri, HttpMethod.GET, null,
                            new ParameterizedTypeReference<DataResponse<SearchResultIdsResponse>>() {});




            // 4. 응답 본문(body)에서 최종 결과를 추출합니다.
            DataResponse<SearchResultIdsResponse> responseBody = res.getBody();
            if (responseBody != null) {
                return responseBody.getData();
            } else {
                // 응답 본문이 null인 경우의 처리
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to call search API server", e);
            // 검색 서버 장애 시, 커뮤니티 검색만이라도 수행하거나, 예외를 던지는 등 정책 결정 필요
            throw new AppException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    private List<CommunityPostListResponse> searchCommunityPosts(List<Long> ids, UserJpaEntity user) {
        try {
            // 공통: 삭제글 제외 후 Map으로 (id → post)
            List<CommunityPost> fetched = communityRepository.findAllById(ids).stream()
                    .filter(post -> !post.isDeleted())
                    .toList();

            Map<Long, CommunityPost> postMap = fetched.stream()
                    .collect(Collectors.toMap(CommunityPost::getId, Function.identity()));

            // 비회원: 비공개글 제외 + 좋아요 항상 false
            if (user == null) {
                return ids.stream()
                        .map(postMap::get)
                        .filter(Objects::nonNull)
                        .filter(post -> !post.isMembersOnly())
                        .map(post -> CommunityPostListResponse.from(post, false))
                        .toList();
            }

            // 회원: 좋아요/차단 적용
            List<Long> likedIds = communityLikeService.getLikedPostIdsByUser(user.getId());
            Set<Long> blockedUserIds = userBlockRepository.findAllByBlockerId(user.getId()).stream()
                    .map(block -> block.getBlocked().getId())
                    .collect(Collectors.toSet());

            return ids.stream()
                    .map(postMap::get)
                    .filter(Objects::nonNull)
                    .map(post -> {
                        boolean isLiked = likedIds.contains(post.getId());
                        boolean isBlocked = blockedUserIds.contains(post.getAuthor().getId());
                        return isBlocked
                                ? CommunityPostListResponse.masked(post, isLiked)
                                : CommunityPostListResponse.from(post, isLiked);
                    })
                    .toList();

        } catch (Exception e) {
            throw new AppException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }


    private List<PerformanceSearchResponse> searchPerformances(List<Long> ids) {
        // fetch join
        List<Performance> performances = performanceRepository.findAllWithTheaterByIdIn(ids);

        Map<Long, Performance> performanceMap = performances.stream()
                .collect(Collectors.toMap(Performance::getId, Function.identity()));

        List<ChatRoom> chatRooms = chatRoomRepository.findByPerformanceIds(ids);
        Map<Long, Long> performanceIdToChatRoomId = chatRooms.stream()
                .collect(Collectors.toMap(
                        cr -> cr.getPerformance().getId(),
                        ChatRoom::getId
                ));

        return ids.stream()
                .map(performanceMap::get)
                .filter(Objects::nonNull)
                .map(p -> PerformanceSearchResponse.from(p, performanceIdToChatRoomId.get(p.getId())))
                .toList();
    }

}
