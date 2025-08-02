package com.example.stagemate.service.search;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.search.SearchDocument;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.PerformanceDetailResponse;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.community.UserBlockRepository;
import com.example.stagemate.repository.performance.PerformanceRepository;
import com.example.stagemate.repository.user.UserJpaRepository;
import com.example.stagemate.service.community.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchTemplate template;
    private final UserJpaRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommunityLikeService communityLikeService;
    private final UserBlockRepository userBlockRepository;
    private final PerformanceRepository performanceRepository;
    private final KeywordService keywordService;

    public void save(SearchDocument doc) {
        template.save(doc);
    }

    public void saveFromCommunity(CommunityPost post) {
        SearchDocument doc = new SearchDocument(
                "community-" + post.getId(),
                "community",
                post.getTitle(),
                post.getContent(),
                null,
                post.getCreatedAt().toLocalDate(),
                null
        );
        save(doc);
    }

    public void deleteFromCommunity(Long postId) {
        template.delete("community-" + postId, SearchDocument.class);
    }


    public List<CommunityPostListResponse> searchCommunityPosts(String keyword, UserJpaEntity user) {

        // redis에 저장
        keywordService.record(keyword);

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
                                .minimumShouldMatch("1") // should 조건 중 최소 1개 이상 만족해야함
                        )
                )
                .withSort(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc)))) // 유사도 순
                .withSort(SortOptions.of(s -> s.field(f -> f.field("startDate").order(SortOrder.Desc)))) // 최신순
                .build();


        List<Long> ids = template.search(query, SearchDocument.class)
                .stream()
                .filter(hit -> hit.getScore() >= 0.1f) // score가 0.1 이상인 결과만 채택해 불필요한 결과 제거
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
    }

    public void saveFromPerformance(Performance performance) {
        SearchDocument doc = new SearchDocument(
                "performance-" + performance.getId(),
                "performance",
                performance.getPerformanceName(),
                null,
                performance.getPerformanceGenre().name(),
                performance.getStartDate(),
                performance.getEndDate()
        );
        save(doc);
    }

    public void deleteAllFromPerformances() {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.term(t -> t.field("type").value("performance")))
                .build();

        List<String> idsToDelete = template.search(query, SearchDocument.class)
                .stream()
                .map(hit -> hit.getContent().getId())
                .toList();

        for (String id : idsToDelete) {
            template.delete(id, SearchDocument.class);
        }
    }

    public List<PerformanceDetailResponse> searchPerformances(String keyword, PerformanceGenre genre, LocalDate date) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> {
                            b.must(m -> m.term(t -> t.field("type").value("performance")));

                            b.should(List.of(
                                    Query.of(q1 -> q1.match(mq -> mq.field("title").query(keyword)))
                            ));

                            if (genre != null) {
                                b.filter(f -> f.term(t -> t.field("genre").value(genre.name())));
                            }

                            if (date != null) {
                                b.filter(f -> f.bool(inner -> inner
                                        .must(m1 -> m1.range(r -> r
                                                .field("startDate")
                                                .lte(JsonData.of(date.toString()))))
                                        .must(m2 -> m2.range(r -> r
                                                .field("endDate")
                                                .gte(JsonData.of(date.toString()))))
                                ));
                            }

                            b.minimumShouldMatch("1");
                            return b;
                        })
                )
                .withSort(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc))))
                .withSort(SortOptions.of(s -> s.field(f -> f.field("startDate").order(SortOrder.Asc)))) // 공연일 빠른 순
                .build();

        List<Long> ids = template.search(query, SearchDocument.class).stream()
                .filter(hit -> hit.getScore() >= 0.1f)
                .map(hit -> hit.getContent().getId())
                .filter(id -> id.startsWith("performance-"))
                .map(id -> Long.parseLong(id.replace("performance-", "")))
                .toList();

        if (ids.isEmpty()) return List.of();

        List<Performance> performances = performanceRepository.findAllById(ids);

        return performances.stream()
                .map(PerformanceDetailResponse::from)
                .toList();
    }

}


