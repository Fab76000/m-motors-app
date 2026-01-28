package com.mmotors.repository;


import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleStatus;
import com.mmotors.entity.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository pour l'entité Vehicle
 */

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Recherche les véhicules par type
     * @param type Type de véhicule (ACHAT ou LOCATION)
     * @return Liste des véhicules du type spécifié
     */
    List<Vehicle> findByType(VehicleType type);

    /**
     * Recherche les véhicules disponibles par type et statut
     * @param type Type de véhicule
     * @param status Statut du véhicule
     * @return Liste des véhicules correspondant aux critères
     */
    List<Vehicle> findByTypeAndStatus(VehicleType type, VehicleStatus status);

    /**
     * Recherche avec filtres multiples et pagination
     * @param type Type de véhicule (null = tous types)
     * @param brand Marque du véhicule (recherche partielle)
     * @param maxPrice Prix maximum
     * @param status Statut du véhicule
     * @param pageable Configuration de pagination
     * @return Page de véhicules correspondant aux critères
     */
    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:type IS NULL OR v.type = :type) AND " +
            "(:brand IS NULL OR UPPER(v.brand) LIKE :brand) AND " +
            "(:maxPrice IS NULL OR " +
            "  (v.type = com.mmotors.entity.VehicleType.ACHAT AND v.price <= :maxPrice) OR " +
            "  (v.type = com.mmotors.entity.VehicleType.LOCATION AND v.monthlyRent <= :maxPrice)) AND " +
            "v.status = :status")
    Page<Vehicle> searchVehicles(
            @Param("type") VehicleType type,
            @Param("brand") String brand,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") VehicleStatus status,
            Pageable pageable
    );

    Long id(Long id);

    /**
    * Compte le nombre de véhicules par statut
    * @param status Statut recherché
    * @return Nombre de véhicules
    */
    long countByStatus(VehicleStatus status);
}
