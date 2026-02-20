package com.mmotors.service;

import com.mmotors.entity.*;
import com.mmotors.repository.DossierRepository;
import com.mmotors.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des dossiers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DossierService {

    private final DossierRepository dossierRepository;
    private final VehicleRepository vehicleRepository;

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

    @Transactional(readOnly = true)
    public Dossier findById(Long id) {
        return dossierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé"));
    }

    /**
     * Trouve un dossier par son numéro de référence
     * @param refernceNumber Numéro de référence
     * @return Dossier trouvé
     */

    @Transactional(readOnly = true)
    public Dossier findByReferenceNumber(String refernceNumber) {
        return dossierRepository.findByReferenceNumber(refernceNumber)
                .orElseThrow(()-> new IllegalArgumentException("Dossier non trouvé"));
    }

    /**
     * Trouve tous les dossiers d'un utilisateur
     * @param user Utilisateur
     * @return Liste des dossiers
     */
    @Transactional(readOnly = true)
    public List<Dossier> findByUser(User user) {
        return dossierRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Recherche des dossiers avec filtres et pagination
     *
     * @param status Statut du dossier (null = tous)
     * @param type Type du dossier (null = tous)
     * @param pageable Pagination
     * @return Page de dossiers
     */
    @Transactional(readOnly = true)
    public Page<Dossier> searchDossiers(DossierStatus status, DossierType type, Pageable pageable) {
        if (status == null && type == null) {
            return dossierRepository.findAll(pageable);
        } else if (status != null && type != null) {
            return dossierRepository.findByStatusAndType(status, type, pageable);
        } else if (status != null) {
            return dossierRepository.findByStatus(status, pageable);
        } else {
            return dossierRepository.findByType(type, pageable);
        }
    }

    /**
     * Valide un dossier (admin uniquement)
     * Change le statut EN_COURS -> VALIDE
     * Change le statut du véhicule DISPONIBLE -> RESERVE
     *
     * @param dossierId ID du dossier
     * @return Dossier validé
     * @throws IllegalArgumentException si le dossier n'existe pas ou n'a pas le statut EN_COURS
     */

    @Transactional
    public Dossier validateDossier(Long dossierId) {
        Dossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé : " + dossierId));
        if (dossier.getStatus() != DossierStatus.EN_COURS) {
            throw new IllegalArgumentException("Seuls les dossiers EN_COURS peuvent être validés");
        }
        dossier.setStatus(DossierStatus.VALIDE);

        Vehicle vehicle = dossier.getVehicle();
        vehicle.setStatus(VehicleStatus.RESERVE);
        vehicleRepository.save(vehicle);

        Dossier savedDossier = dossierRepository.save(dossier);
        log.info("Dossier validé : {} (véhicule {} passé en RESERVE)",
                dossier.getReferenceNumber(), vehicle.getId());

        return savedDossier;
    }

    /**
     * Rejette un dossier (admin uniquement)
     * Change le statut EN_COURS -> REJETE
     * ENregistre la raison du rejet
     *
     * @param dossierId ID du dossier
     * @param rejectionReason Raison du rejet
     * @return Dossier rejeté
     * @throws IllegalArgumentException si le dossier n'existe pas ou n'est pas EN_COURS
 */
    @Transactional
    public Dossier rejectDossier(Long dossierId, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new IllegalArgumentException("La raison du rejet est obligatoire");
        }

        Dossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé : " + dossierId));

        if (dossier.getStatus() != DossierStatus.EN_COURS) {
            throw new IllegalArgumentException("Seuls les dossiers EN_COURS peuvent être rejetés");
        }

        dossier.setStatus(DossierStatus.REJETE);
        dossier.setRejectionReason(rejectionReason);


        Dossier savedDossier = dossierRepository.save(dossier);

        log.info("Dossier rejeté : {} (raison: {})",
                dossier.getReferenceNumber(), rejectionReason);

        return savedDossier;
    }
}

