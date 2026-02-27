package com.mmotors.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    /**
     * Simule l'envoi d'un email (loggué en dev)
     */
    public void sendEmail(String to, String subject, String body) {
        log.info("=== EMAIL ===");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("=============");
    }
}
