package com.example.stagemate.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "StageMate API",
                version = "v1.0.0",
                description = "스테이지메이트 API 목록입니다."
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "https://api.stagemate.co.kr", description = "Production Server"),
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "http://api.stagemate.co.kr", description = "HTTP Server"),
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "http://localhost:8080", description = "Local Server")
        }
)
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info().title("StageMate API").version("v1.0.0").description("스테이지메이트 API 목록입니다."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .servers(List.of(
                        new Server().url("https://api.stagemate.co.kr").description("Production Server"),
                        new Server().url("http://api.stagemate.co.kr").description("HTTP Server"),
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }
}
