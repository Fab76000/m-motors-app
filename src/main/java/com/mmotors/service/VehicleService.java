package com.mmotors.service;

import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleStatus;
import com.mmotors.entity.VehicleType;
import com.mmotors.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
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
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Recherche des véhicules avec filtres et pagination
     * @param type Type de véhicule (null= tous)
     * @param brand Marque (null = toutes)
     * @param maxPrice Prix maximum (null = pas de limite)
     * @param page Numéro de page (0-indexed)
     * @param size Nombre de résultats par page
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
     * @Return Nombre de véhicules
     */
    @Transactional(readOnly = true)
    public long count() {
        return vehicleRepository.count();
    }

    /**
     * Compte le nombre de véhicules par statut
     * @param status Statut recherché
     * @return Nombre de véhicules
     */
    @Transactional(readOnly = true)
    public long countByStatus(VehicleStatus status) {
        return vehicleRepository.countByStatus(status);
    }
        /**
         * Sauvegarde un véhicule
         * @param vehicle Véhicule à sauvegarder
         * @return Véhicule sauvegardé
         */

        @Transactional
        public Vehicle save (Vehicle vehicle) {
            return vehicleRepository.save(vehicle);
        }

    /**
     * Récupère tous les véhicules
     * @return Liste de tous les véhicules
     */
    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    /**
     * Bascule un véhicule entre ACHAT et LOCATION
     *
     * @param id ID du véhicule
     * @param newPriceOrRent Nouveau prix (si passage en ACHAT) ou loyer (si passage en LOCATION)
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

}
