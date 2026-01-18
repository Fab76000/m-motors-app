package com.mmotors.repository;

import com.mmotors.entity.Document;
import com.mmotors.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Document
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Trouve tous les documents associés à un dossier
     * @param dossier Dossier parent
     * @return Liste des documents du dossier
     */
    List<Document> findByDossier(Dossier dossier);

    /**
     * Compte le nombre de documents d'un dossier
     * @param dossier Dossier dont on compte les documents
     * @return Nombre de documents
     */
    long countByDossier(Dossier dossier);
}
