package com.example.stagemate.global.auth.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class GmailSmtpSenderCustom implements CustomMailSender {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {

        System.out.println("📧 메일 보낼 때 사용하는 mailSender = " + mailSender.getClass().getName());
        System.out.println("📧 Username = " + ((JavaMailSenderImpl) mailSender).getUsername());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
