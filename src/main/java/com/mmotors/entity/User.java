package com.mmotors.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un utilisateur (client ou administrateur)
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 30, message = "Le nom ne peut pas dépasser 30 caractères")
    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 20, message = "Le prénom ne peut pas dépasser 20 caractères")
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;


    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères")
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @NotNull
    @Column(name = "rgpd_consent", nullable = false)
    private Boolean rgpdConsent = false;

    @Column(name = "rgpd_consent_date")
    private LocalDateTime rgpdConsentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Méthode appelée avant la persistance en base
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (rgpdConsent && rgpdConsentDate == null) {
            rgpdConsentDate = LocalDateTime.now();
        }
    }
}
