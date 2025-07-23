package com.example.stagemate.global.auth.mail;

public interface CustomMailSender {
    void send(String to, String subject, String body);
}
