package com.mmotors.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertingService Tests")
public class AlertingServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AlertingService alertingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(alertingService, "adminEmail", "admin@mmotors.fr");
        ReflectionTestUtils.setField(alertingService, "lastAlertTimestamp",
                new java.util.concurrent.atomic.AtomicLong(0));
    }

    @Test
    @DisplayName("sendCriticalAlert - Envoie alerte email")
    void sendCriticalAlert_SendsEmail() {
        alertingService.sendCriticalAlert("Erreur 500", "Test erreur", new RuntimeException("test"));

        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("sendCriticalAlert - Anti-spam bloque deuxième alerte")
    void sendCriticalAlert_AntiSpam_BlocksSecondAlert() {
        alertingService.sendCriticalAlert("Erreur 500", "Test erreur", null);
        alertingService.sendCriticalAlert("Erreur 500", "Test erreur", null);

        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("sendCriticalAlert - Fonctionne sans exception")
    void sendCriticalAlert_WithoutException_Works() {
        alertingService.sendCriticalAlert("Erreur BDD", "Connexion échouée", null);

        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }
}
