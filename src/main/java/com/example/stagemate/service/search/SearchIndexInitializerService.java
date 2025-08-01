package com.example.stagemate.service.search;

import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.search.SearchDocument;
import com.example.stagemate.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SearchIndexInitializerService {

    private final CommunityRepository communityRepository;
    private final ElasticsearchTemplate template;

    // 애플리케이션 시작 시 db에 존재하는 엘라스틱서치 인덱스 작업
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void indexAllOnStartup() {
        indexAllCommunityPosts();
        log.info("애플리케이션 시작 시 엘라스틱서치 커뮤니티 게시글 초기화 완료");
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
                        null
                )).toList();

        template.save(documents); // bulk insert
    }
}

