package com.mmotors.service;

import com.mmotors.entity.*;
import com.mmotors.repository.DossierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des dossiers
 */
@Service
@RequiredArgsConstructor
public class DossierService {

    private final DossierRepository dossierRepository;

    /**
     * Crée un nouveau dossier
     * @param user Utilisateur propriétaire
     * @param vehicle Véhicule concerné
     * @param dossierType Type de dossier (ACHAT ou LOCATION)
     * @param paymentMode Mode de paiement (si ACHAT)
     * @param tradeIn Reprise ancien véhicule (si ACHAT)
     * @param duration Durée en mois (si LOCATION)
     * @return Dossier crée
     */

    @Transactional
    public Dossier createDossier(User user, Vehicle vehicle, DossierType dossierType, String paymentMode, Boolean tradeIn, Integer duration) {

        if (vehicle.getStatus() != VehicleStatus.DISPONIBLE) {
            throw new IllegalStateException("Ce véhicule n'est plus disponible");
        }
        Dossier dossier = new Dossier();
        dossier.setUser(user);
        dossier.setVehicle(vehicle);
        dossier.setType(dossierType);
        dossier.setStatus(DossierStatus.EN_COURS);
        dossier.setCreatedAt(LocalDateTime.now());

        dossier.setReferenceNumber(generateReferenceNumber());

        if (dossierType == DossierType.ACHAT) {
            dossier.setPaymentMode(paymentMode);
            dossier.setTradeIn(tradeIn);
        } else if (dossierType == DossierType.LOCATION) {
            dossier.setDuration(duration);
        }
        return dossierRepository.save(dossier);

    }

    /**
     * Génère un numéro de référence unique (format : DOSS-YYYY-XXXXX)
     * @return Numéro de référence
     */

    private String generateReferenceNumber() {
        int year = LocalDateTime.now().getYear();
        long count = dossierRepository.count() + 1;
        return String.format("DOSS-%d-%05d", year, count);
    }

    /**
     * Trouve un dossier par son ID
     * @param id Identifiant du dossier
     * @return Dossier trouvé
     */

    public Dossier findById(Long id) {
        return dossierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé"));
    }

    /**
     * Trouve un dossier par son numéro de référence
     * @param refernceNumber Numéro de référence
     * @return Dossier trouvé
     */

    public Dossier findByReferenceNumber(String refernceNumber) {
        return dossierRepository.findByReferenceNumber(refernceNumber)
                .orElseThrow(()-> new IllegalArgumentException("Dossier non trouvé"));
    }

    /**
     * Trouve tous les dossiers d'un utilisateur
     * @param user Utilisateur
     * @return Liste des dossiers
     */

    public List<Dossier> findByUser(User user) {
        return dossierRepository.findByUserOrderByCreatedAtDesc(user);
    }
}

