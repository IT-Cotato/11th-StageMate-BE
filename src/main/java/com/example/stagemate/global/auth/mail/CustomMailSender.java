package com.example.stagemate.global.auth.mail;

public interface CustomMailSender {
    // 일반 텍스트 메일
    void send(String to, String subject, String text);

    // HTML 메일
    void sendHtml(String to, String subject, String html);

    // HTML 메일 + Reply-To
    void sendHtmlWithReplyTo(String to, String subject, String html, String replyTo);
}
