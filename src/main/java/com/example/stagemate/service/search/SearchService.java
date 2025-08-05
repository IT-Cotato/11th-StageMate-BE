package com.example.stagemate.service.search;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.example.stagemate.domain.chat.ChatRoom;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.search.SearchDocument;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.dto.response.search.PerformanceSearchResponse;
import com.example.stagemate.dto.response.search.SearchResultResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.chat.ChatRoomRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.community.UserBlockRepository;
import com.example.stagemate.repository.performance.PerformanceRepository;
import com.example.stagemate.service.community.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.stagemate.global.exception.search.SearchErrorCode.ELASTICSEARCH_ERROR;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchTemplate template;
    private final CommunityRepository communityRepository;
    private final CommunityLikeService communityLikeService;
    private final UserBlockRepository userBlockRepository;
    private final PerformanceRepository performanceRepository;
    private final KeywordService keywordService;
    private final ChatRoomRepository chatRoomRepository;

    public void save(SearchDocument doc) {
        try {
            template.save(doc);
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }

    public void saveFromCommunity(CommunityPost post) {
        SearchDocument doc = new SearchDocument(
                "community-" + post.getId(),
                "community",
                post.getTitle(),
                post.getContent(),
                null,
                null,
                null,
                post.getCreatedAt().toLocalDate()
        );
        save(doc);
    }

    public void deleteFromCommunity(Long postId) {
        try {
            template.delete("community-" + postId, SearchDocument.class);
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }


    public List<CommunityPostListResponse> searchCommunityPosts(String keyword, UserJpaEntity user) {

        // redis에 저장
        keywordService.record(keyword);

        try {
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .bool(b -> b
                                    .must(m -> m.term(t -> t.field("type").value("community")))
                                    .should(List.of(
                                            Query.of(q1 -> q1.match(m -> m
                                                    .field("title")
                                                    .query(keyword)
                                            )),
                                            Query.of(q2 -> q2.match(m -> m
                                                    .field("content")
                                                    .query(keyword)
                                            ))
                                    ))
                                    .minimumShouldMatch("1")
                            )
                    )
                    .withSort(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc)))) // 유사도 순
                    .withSort(SortOptions.of(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc)))) // 최신순
                    .withMaxResults(30) // 최대 30개 보여주기
                    .build();


            List<Long> ids = template.search(query, SearchDocument.class)
                    .stream()
//                    .filter(hit -> hit.getScore() >= 0.1f) // score가 0.1 이상인 결과만 채택해 불필요한 결과 제거
                    .map(hit -> hit.getContent().getId())
                    .filter(id -> id.startsWith("community-"))
                    .map(id -> Long.parseLong(id.replace("community-", "")))
                    .toList();

            if (ids.isEmpty()) return List.of();

            // 엘라스틱 서치에서 조회된 결과 게시글 가져오기
            List<CommunityPost> posts;

            // 순서를 유지하기 위해 stream으로 필터링
            // 비회원인 경우
            if (user == null) {
                posts = communityRepository.findAllById(ids).stream()
                        .filter(post -> !post.isDeleted() && !post.isMembersOnly())
                        .toList();

                return posts.stream()
                        .map(post -> CommunityPostListResponse.from(post, false)) // 비회원은 좋아요 없음
                        .toList();
            }


            // 회원인 경우
            // 좋아요, 차단한 유저 체크
            posts = communityRepository.findAllById(ids).stream()
                    .filter(post -> !post.isDeleted())
                    .toList();
            List<Long> likedIds = communityLikeService.getLikedPostIdsByUser(user.getId());
            Set<Long> blockedUserIds = userBlockRepository.findAllByBlockerId(user.getId()).stream()
                    .map(block -> block.getBlocked().getId())
                    .collect(Collectors.toSet());

            return posts.stream()
                    .map(post -> {
                        boolean isLiked = likedIds.contains(post.getId());
                        boolean isBlocked = blockedUserIds.contains(post.getAuthor().getId());

                        return isBlocked
                                ? CommunityPostListResponse.masked(post, isLiked)
                                : CommunityPostListResponse.from(post, isLiked);
                    })
                    .toList();
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }

    public void saveFromPerformance(Performance performance) {
        SearchDocument doc = new SearchDocument(
                "performance-" + performance.getId(),
                "performance",
                performance.getPerformanceName(),
                null,
                performance.getPerformanceGenre().name(),
                performance.getStartDate(),
                performance.getEndDate(),
                null
        );
        save(doc);
    }


    public void deleteFromPerformanceId(Long performanceId) {
        try {
            template.delete("performance-" + performanceId, SearchDocument.class);
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }

    public List<PerformanceSearchResponse> searchPerformances(String keyword, PerformanceGenre genre, LocalDate date) {
        try {
            boolean hasShould = (keyword != null && !keyword.isBlank());
            LocalDate today = LocalDate.now();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .bool(b -> {
                                b.must(m -> m.term(t -> t.field("type").value("performance")));

                                if (hasShould) {
                                    b.should(List.of(
                                            Query.of(q1 -> q1.match(mq -> mq.field("title").query(keyword)))
                                    ));
                                }

                                if (genre != null) {
                                    b.filter(f -> f.match(m -> m.field("genre").query(genre.name())));
                                }

                                if (date != null) {
                                    b.filter(f -> f.bool(inner -> inner
                                            .must(m1 -> m1.range(r -> r.field("startDate").lte(JsonData.of(date.toString()))))
                                            .must(m2 -> m2.range(r -> r.field("endDate").gte(JsonData.of(date.toString()))))
                                    ));
                                } else {
                                    b.filter(f -> f.bool(inner -> inner
                                            .should(List.of(
                                                    Query.of(q1 -> q1.range(r -> r.field("startDate").gt(JsonData.of(today.toString())))),
                                                    Query.of(q2 -> q2.bool(b2 -> b2
                                                            .must(m1 -> m1.range(r -> r.field("startDate").lte(JsonData.of(today.toString()))))
                                                            .must(m2 -> m2.range(r -> r.field("endDate").gte(JsonData.of(today.toString()))))
                                                    ))
                                            ))
                                            .minimumShouldMatch("1")
                                    ));
                                }

                                if (hasShould) {
                                    b.minimumShouldMatch("1");
                                }

                                return b;
                            })
                    )
                    .withSort(SortOptions.of(s -> s.field(f -> f.field("startDate").order(SortOrder.Asc))))
                    .withMaxResults(30)
                    .build();

            List<Long> ids = template.search(query, SearchDocument.class).stream()
                    .map(hit -> hit.getContent().getId())
                    .filter(id -> id.startsWith("performance-"))
                    .map(id -> Long.parseLong(id.replace("performance-", "")))
                    .toList();

            if (ids.isEmpty()) return List.of();

            List<Performance> performances = performanceRepository.findAllById(ids);

            Map<Long, Performance> performanceMap = performances.stream()
                    .collect(Collectors.toMap(Performance::getId, Function.identity()));

            List<ChatRoom> chatRooms = chatRoomRepository.findByPerformanceIds(ids);
            Map<Long, Long> performanceIdToChatRoomId = chatRooms.stream()
                    .collect(Collectors.toMap(
                            chatRoom -> chatRoom.getPerformance().getId(),
                            ChatRoom::getId
                    ));

            // Elasticsearch에서 받아온 id 순서대로 정확히 다시 정렬해서 반환
            return ids.stream()
                    .filter(performanceMap::containsKey)
                    .map(id -> {
                        Performance performance = performanceMap.get(id);
                        Long chatRoomId = performanceIdToChatRoomId.get(id);
                        return PerformanceSearchResponse.from(performance, chatRoomId);
                    })
                    .toList();

        } catch (Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }

    public SearchResultResponse searchAll(String keyword, PerformanceGenre genre, LocalDate date, UserJpaEntity user) {
        List<CommunityPostListResponse> communityResult = null;

        // 키워드가 있을 경우 커뮤니티 검색 수행
        if (keyword != null && !keyword.isBlank()) {
            communityResult = searchCommunityPosts(keyword, user);
        }

        // 공연은 항상 검색 (keyword만 null이어도 장르, 날짜로 검색할 수 있으므로)
        List<PerformanceSearchResponse> performanceResult = searchPerformances(keyword, genre, date);

        return SearchResultResponse.of(performanceResult, communityResult);
    }


}


