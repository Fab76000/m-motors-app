package com.mmotors.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("sendEmail - Log email sans exception")
    void sendEmail_LogsWithoutException() {
        assertThatNoException().isThrownBy(() ->
                emailService.sendEmail("test@mmotors.fr", "Sujet test", "Corps du message")
        );
    }

    @Test
    @DisplayName("sendEmail - Fonctionne avec contenu vide")
    void sendEmail_WithEmptyContent_NoException() {
        assertThatNoException().isThrownBy(() ->
                emailService.sendEmail("", "", "")
        );
    }
}
