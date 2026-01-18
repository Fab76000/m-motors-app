package com.mmotors.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un dossier de demande (achat ou location)
 */

@Entity
@Table(name = "dossier", indexes = {
        @Index(name = "idx_dossier_user", columnList = "user_id"),
        @Index(name = "idx_dossier_status", columnList = "status"),
        @Index(name = "idx_dossier_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de référence est obligatoire")
    @Size(max = 50)
    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DossierType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DossierStatus status = DossierStatus.EN_COURS;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Size(max = 50)
    @Column(name = "payment_mode", length = 50)
    private String paymentMode;

    @Column(name = "trade_in")
    private Boolean tradeIn;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        // Générer le numéro de référence si pas encore défini
        if (referenceNumber == null) {
            generateReferenceNumber();
        }
    }

    /**
     * Génère un numéro de référence unique (format : DOSS-YYYY-NNNNN)
     */
    private void generateReferenceNumber() {
        int year = LocalDateTime.now().getYear();
        long timestamp = System.currentTimeMillis() % 100000;
        referenceNumber = String.format("DOSS-%d-%05d", year, timestamp);
    }
}
