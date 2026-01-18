package com.mmotors.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entité représentant un véhicule (à vendre ou en location)
 */
@Entity
@Table(name = "vehicle", indexes = {
        @Index(name = "idx_vehicle_type", columnList = "type"),
        @Index(name = "idx_vehicle_brand", columnList = "brand"),
        @Index(name = "idx_vehicle_price", columnList = "price")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "L'année est obligatoire")
    @Min(value = 1900, message = "L'année doit être supérieure à 1900")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Le kilométrage est obligatoire")
    @Min(value = 0, message = "Le kilométrage ne peut pas être négatif")
    @Column(nullable = false)
    private Integer mileage;

    @NotBlank(message = "Le type de carburant est obligatoire")
    @Size(max = 50)
    @Column(name = "fuel_type", nullable = false, length = 50)
    private String fuelType;

    @NotNull(message = "La puissance est obligatoire")
    @Min(value = 1, message = "La puissance doit être supérieure à 0")
    @Column(nullable = false)
    private Integer power;

    @NotBlank(message = "Le type de boîte est obligatoire")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String gearbox;

    @NotNull(message = "Le nombre de portes est obligatoire")
    @Min(value = 2, message = "Le nombre de portes doit être au moins 2")
    @Column(nullable = false)
    private Integer doors;

    @NotBlank(message = "La couleur est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String color;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "monthly_rent", precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.DISPONIBLE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Les photos sont obligatoires")
    @Column(name = "photo_paths", nullable = false, columnDefinition = "TEXT")
    private String photoPaths;

    /**
     * Validation : Si ACHAT, price obligatoire. Si LOCATION, monthlyRent obligatoire.
     */
    @PrePersist
    @PreUpdate
    protected void validatePricing() {
        if (type == VehicleType.ACHAT && price == null) {
            throw new IllegalStateException("Le prix est obligatoire pour un véhicule à vendre");
        }
        if (type == VehicleType.LOCATION && monthlyRent == null) {
            throw new IllegalStateException("Le loyer mensuel est obligatoire pour un véhicule en location");
        }
    }
}
