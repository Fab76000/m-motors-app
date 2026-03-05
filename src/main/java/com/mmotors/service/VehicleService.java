package com.mmotors.service;

import com.mmotors.entity.DossierStatus;
import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleStatus;
import com.mmotors.entity.VehicleType;
import com.mmotors.repository.DossierRepository;
import com.mmotors.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service de gestion des véhicules
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DossierRepository dossierRepository;

    /**
     * Recherche des véhicules avec filtres et pagination
     *
     * @param type     Type de véhicule (null= tous)
     * @param brand    Marque (null = toutes)
     * @param maxPrice Prix maximum (null = pas de limite)
     * @param page     Numéro de page (0-indexed)
     * @param size     Nombre de résultats par page
     * @return Page de véhicules
     */
    @Transactional(readOnly = true)
    public Page<Vehicle> searchVehicles(VehicleType type, String brand, BigDecimal maxPrice, int page, int size) {
        String searchBrand = null;
        if (brand != null && !brand.trim().isEmpty()) {
            searchBrand = "%" + brand.trim().toUpperCase() + "%";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.searchVehicles(type, searchBrand, maxPrice, VehicleStatus.DISPONIBLE, pageable);
    }

    /**
     * Trouve un véhicule par son ID
     *
     * @param id Identifiant du véhicule
     * @return Véhicule trouvé
     */
    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé avec cet ID"));
    }

    /**
     * Compte le nombre total de véhicules
     *
     * @return the long
     * @Return Nombre de véhicules
     */
    @Transactional(readOnly = true)
    public long count() {
        return vehicleRepository.count();
    }

    /**
     * Compte le nombre de véhicules par statut
     *
     * @param status Statut recherché
     * @return Nombre de véhicules
     */
    @Transactional(readOnly = true)
    public long countByStatus(VehicleStatus status) {
        return vehicleRepository.countByStatus(status);
    }

    /**
     * Sauvegarde un véhicule
     *
     * @param vehicle Véhicule à sauvegarder
     * @return Véhicule sauvegardé
     */
    @Transactional
        public Vehicle save (Vehicle vehicle) {
            return vehicleRepository.save(vehicle);
        }

    /**
     * Récupère tous les véhicules
     *
     * @return Liste de tous les véhicules
     */
    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    /**
     * Bascule un véhicule entre ACHAT et LOCATION
     *
     * @param id             ID du véhicule
     * @param newPriceOrRent Nouveau prix (si passage en ACHAT) ou mensualité (si passage en LOCATION)
     * @return Véhicule modifié
     */
    @Transactional
    public Vehicle switchVehicleType(Long id, BigDecimal newPriceOrRent) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé avec l'ID : " + id));

        if (vehicle.getType() == VehicleType.ACHAT) {
            // ACHAT → LOCATION
            vehicle.setType(VehicleType.LOCATION);
            vehicle.setPrice(null);
            vehicle.setMonthlyRent(newPriceOrRent);
        } else {
            // LOCATION → ACHAT
            vehicle.setType(VehicleType.ACHAT);
            vehicle.setMonthlyRent(null);
            vehicle.setPrice(newPriceOrRent);
        }

        return vehicleRepository.save(vehicle);
    }

    /**
     * Met à jour un véhicule existant
     * RÈGLE MÉTIER : La modification est bloquée si le véhicule a des dossiers EN_COURS ou VALIDE
     * Les véhicules avec uniquement des dossiers REJETE peuvent être modifiés
     *
     * @param id ID du véhicule à modifier
     * @param updatedVehicle Véhicule avec les nouvelles données
     * @return Véhicule mis à jour
     * @throws IllegalArgumentException si le véhicule n'existe pas
     * @throws IllegalArgumentException si des dossiers actifs (EN_COURS, VALIDE) sont liés
     */
    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé : " + id));
        // Vérifier qu'aucun dossier actif n'est lié (seuls les dossiers rejetés sont tolérés)
        long activeDossierCount = dossierRepository.countByVehicleIdAndStatusIn(
                id,
                java.util.List.of(DossierStatus.EN_COURS, DossierStatus.VALIDE)
        );
        if (activeDossierCount > 0) {
            throw new IllegalArgumentException(
                    "Impossible de modifier ce véhicule : " + activeDossierCount + " dossier(s) actif(s) y sont liés. " +
                            "Vous pouvez modifier un véhicule uniquement s'il n'a que des dossiers rejetés."
            );
        }
        existingVehicle.setType(updatedVehicle.getType());
        existingVehicle.setBrand(updatedVehicle.getBrand());
        existingVehicle.setModel(updatedVehicle.getModel());
        existingVehicle.setYear(updatedVehicle.getYear());
        existingVehicle.setMileage(updatedVehicle.getMileage());
        existingVehicle.setFuelType(updatedVehicle.getFuelType());
        existingVehicle.setPower(updatedVehicle.getPower());
        existingVehicle.setGearbox(updatedVehicle.getGearbox());
        existingVehicle.setDoors(updatedVehicle.getDoors());
        existingVehicle.setColor(updatedVehicle.getColor());
        existingVehicle.setPrice(updatedVehicle.getPrice());
        existingVehicle.setMonthlyRent(updatedVehicle.getMonthlyRent());
        existingVehicle.setDescription(updatedVehicle.getDescription());
        existingVehicle.setStatus(updatedVehicle.getStatus());

        // Sauvegarder
        Vehicle savedVehicle = vehicleRepository.save(existingVehicle);

        log.info("Véhicule mis à jour : {} {} (ID: {})",
                savedVehicle.getBrand(), savedVehicle.getModel(), savedVehicle.getId());

        return savedVehicle;
    }

    /**
     * Supprime un véhicule
     * ATTENTION : Impossible si des dossiers sont liés
     *
     * @param id ID du véhicule
     * @throws IllegalArgumentException si le véhicule n'existe pas ou a des dossiers liés
     */
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé : " + id));

        try {
            vehicleRepository.delete(vehicle);
            log.info("Véhicule supprimé : {} {} (ID: {})",
                    vehicle.getBrand(), vehicle.getModel(), vehicle.getId());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Impossible de supprimer ce véhicule : des dossiers y sont liés. " +
                            "Veuillez d'abord traiter ou supprimer les dossiers associés."
            );
        }
    }
}
