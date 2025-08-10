package com.example.stagemate.global.config;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender(MailProperties props) {
        JavaMailSenderImpl s = new JavaMailSenderImpl();
        s.setHost(props.getHost());
        s.setPort(props.getPort());
        s.setUsername(props.getUsername());
        s.setPassword(props.getPassword());
        // mail.smtp.* 붙은 추가 속성들 전달
        Properties p = s.getJavaMailProperties();
        p.putAll(props.getProperties());
        return s;
    }
}
