package com.mmotors.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.beans.JavaBean;

@Slf4j
@Service
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.from}")
    private String fromEmail;

    /**
     * Envoie un email via SMTP (Mailjet en prod, loggué en dev)
     */

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email envoyé à  {} - sujet: {}", to, subject);
        } catch (Exception e) {
            log.error("Échec envoi email à {} : {}", to, e.getMessage());
        }
    }
}
