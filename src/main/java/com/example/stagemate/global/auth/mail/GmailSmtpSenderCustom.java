package com.example.stagemate.global.auth.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class GmailSmtpSenderCustom implements CustomMailSender {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendHtml(String to, String subject, String html) {
        try {
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new RuntimeException("HTML 메일 발송 실패", e);
        }
    }


    @Override
    public void sendHtmlWithReplyTo(String to, String subject, String html, String replyTo) {
        try {
            var msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setReplyTo(replyTo);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 실패", e);
        }
    }
}
