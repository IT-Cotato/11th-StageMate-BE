package com.example.stagemate.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AuthConfigLogger {

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @PostConstruct
    public void printSecretCheck() {
        System.out.println("✅ client-secret: " + clientSecret);
    }
}
