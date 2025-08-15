package com.example.stagemate.global.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 커넥션 풀
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(60)) // HttpClient5는 TimeValue 사용
                .build();

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        // ms 단위(스프링 6에서도 그대로 동작)
        factory.setConnectTimeout(3000);
        factory.setConnectionRequestTimeout(2000);

        return new RestTemplate(factory); // <-- org.springframework.web.client.RestTemplate
    }
}
