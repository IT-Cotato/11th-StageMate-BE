package com.example.stagemate.global.auth;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieSameSiteConfig {

    @Bean
    public TomcatContextCustomizer sameSiteCookieConfig() {
        return context -> {
            Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies("None"); // ✅ HTTP 환경에서 허용됨 -> 추후에 https 환경에서 None 설정
            context.setCookieProcessor(cookieProcessor);
        };
    }
}
