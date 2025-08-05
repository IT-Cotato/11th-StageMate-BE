package com.example.stagemate.service.search;

import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.search.SearchDocument;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.community.CommunityRepository;
import com.example.stagemate.repository.performance.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.stagemate.global.exception.search.SearchErrorCode.ELASTICSEARCH_ERROR;

@RequiredArgsConstructor
@Service
@Slf4j
public class SearchIndexInitializerService {

    private final CommunityRepository communityRepository;
    private final ElasticsearchTemplate template;
    private final PerformanceRepository performanceRepository;

    // 애플리케이션 시작 시 db에 존재하는 엘라스틱서치 인덱스 작업
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void indexAllOnStartup() {
        if (!template.indexOps(SearchDocument.class).exists()) {
            log.info("🔍 search 인덱스가 없어 전체 초기화 진행");
            indexAllCommunityPosts();
            indexAllPerformances();
            log.info("✅ 애플리케이션 시작 시 Elasticsearch 초기화 완료");
        } else {
            log.info("✅ search 인덱스가 이미 존재합니다. 초기화 없이 실행합니다.");
        }
    }

    public void indexAllCommunityPosts() {
        List<CommunityPost> posts = communityRepository.findAll();

        List<SearchDocument> documents = posts.stream()
                .map(post -> new SearchDocument(
                        "community-" + post.getId(),
                        "community",
                        post.getTitle(),
                        post.getContent(),
                        null,
                        null,
                        null,
                        post.getCreatedAt().toLocalDate()
                )).toList();

        try {
            template.save(documents); // bulk insert
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }

    public void indexAllPerformances() {
        List<Performance> performances = performanceRepository.findAll();

        List<SearchDocument> documents = performances.stream()
                .map(performance -> new SearchDocument(
                        "performance-" + performance.getId(),
                        "performance",
                        performance.getPerformanceName(),
                        null,
                        performance.getPerformanceGenre().name(),
                        performance.getStartDate(),
                        performance.getEndDate(),
                        null
                )).toList();

        try {
            template.save(documents); // bulk insert
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }

    public void deleteAllFromPerformances() {
        try {
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
        } catch(Exception e) {
            throw new AppException(ELASTICSEARCH_ERROR);
        }
    }
}

