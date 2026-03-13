package com.mmotors.repository;

import com.mmotors.entity.Dossier;
import com.mmotors.entity.DossierStatus;
import com.mmotors.entity.DossierType;
import com.mmotors.entity.User;
import jakarta.persistence.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Dossier
 */
@Repository
public interface DossierRepository extends JpaRepository<Dossier, Long> {

    /**
     * Trouve tous les dossiers d'un utilisateur triés par date décroissante
     * @param user Utilisateur propriétaire des dossiers
     * @return Liste des dossiers de l'utilisateur
     */
    List<Dossier> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Trouve un dossier par son numéro de référence unique
     * @param referenceNumber Numéro de référence du dossier
     * @return Optional contenant le dossier si trouvé
     */
    Optional<Dossier> findByReferenceNumber(String referenceNumber);

    /**
     * Recherche par statut avec pagination
     */
    Page<Dossier> findByStatus(DossierStatus status, Pageable pageable);

    /**
     * Recherche par type avec pagination
     */
    Page<Dossier> findByType(DossierType type, Pageable pageable);

    /**
     * Recherche par statut ET type avec pagination
     */
    Page<Dossier> findByStatusAndType(DossierStatus status, DossierType type, Pageable pageable);

    /**
     * Compte le nombre de dossiers actifs (non rejetés) liés à un véhicule
     * Statuts considérés : EN_COURS, VALIDE
     *
     * @param vehicleId ID du véhicule
     * @param statuses Liste des statuts à compter (EN_COURS, VALIDE)
     * @return Nombre de dossiers actifs associés à ce véhicule
     */
    long countByVehicleIdAndStatusIn(Long vehicleId, List<DossierStatus> statuses);

    /**
     * Recherche un dossier par son ID avec ses relations (véhicule et utilisateur)
     *
     * @param id ID du dossier
     * @return Dossier avec ses relations (véhicule et utilisateur)
     */
    @Query("SELECT d FROM Dossier d JOIN FETCH d.vehicle JOIN FETCH d.user WHERE d.id = :id ")
    Optional<Dossier> findByIdWithVehicleAndUser(Long id);

    /**
     * Trouve tous les dossiers d'un utilisateur avec le véhicule chargé,
     * triés par date de création décroissante
     * (évite le LazyInitializationException sur vehicle en dehors de la session JPA)
     * @param user Utilisateur propriétaire des dossiers
     * @return Liste des dossiers avec vehicle chargé
     */
    @Query("SELECT d FROM Dossier d JOIN FETCH d.vehicle WHERE d.user = :user ORDER BY d.createdAt DESC")
    List<Dossier> findByUserWithVehicle(@Param("user") User user);
}
