package com.mmotors.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertingService {

    private final EmailService emailService;

    @Value("${app.admin.email}")
    private String adminEmail;

    // Anti-spam : 1 email max toutes les 5 minutes
    private final AtomicLong lastAlertTimestamp = new AtomicLong(0);
    private static final long ALERT_COOLDOWN_MS = 5 * 60 * 1000;

    /**
     * Envoie une alerte email à l'admin en cas d'erreur critique
     */
    public void sendCriticalAlert(String errorType, String message, Exception ex) {
        long now = System.currentTimeMillis();
        if (now - lastAlertTimestamp.get() < ALERT_COOLDOWN_MS) {
            log.warn("[ALERTING] Alerte supprimée (anti-spam) : {}", errorType);
            return;
        }
        lastAlertTimestamp.set(now);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String body = String.format("""
                [M-Motors] Alerte critique - %s
                
                Timestamp : %s
                Type : %s
                Message : %s
                Exception : %s
                """,
                errorType, timestamp, errorType, message,
                ex != null ? ex.getClass().getSimpleName() + " - " + ex.getMessage() : "N/A"
        );

        emailService.sendEmail(adminEmail, "[M-Motors] Alerte : " + errorType, body);
        log.error("[ALERTING] Alerte envoyée à {} : {}", adminEmail, errorType);
    }
}
