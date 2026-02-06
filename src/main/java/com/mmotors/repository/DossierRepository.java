package com.mmotors.repository;

import com.mmotors.entity.Dossier;
import com.mmotors.entity.DossierStatus;
import com.mmotors.entity.DossierType;
import com.mmotors.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Trouve les dossiers par statut
     * @param status Statut des dossiers recherchés
     * @return Liste des dossiers ayant ce statut
     */
    List<Dossier> findByStatus(DossierStatus status);

    /**
     * Compte le nombre de dossiers d'un utilisateur
     * @param user Utilisateur dont on compte les dossiers
     * @return Nombre de dossiers
     */
    long countByUser(User user);

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
}
