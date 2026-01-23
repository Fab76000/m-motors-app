package com.mmotors.service;

import com.mmotors.entity.Document;
import com.mmotors.entity.DocumentType;
import com.mmotors.entity.Dossier;
import com.mmotors.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des documents
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    // Répertoire de stockage des documents (hors webroot pour sécurité)
    private static final String UPLOAD_DIR = "uploads/documents/";

    // Taille maximale d'un fichier : 5 Mo
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Types MIME autorisés
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    /**
     * Sauvegarde un document uploadé
     *
     * @param file     Fichier uploadé
     * @param fileType Type de document
     * @param dossier  Dossier parent
     * @return Document sauvegardé
     */
    @Transactional
    public Document saveDocument(MultipartFile file, DocumentType fileType, Dossier dossier)
            throws IOException {

        // Validation du fichier
        validateFile(file);

        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique pour éviter les collisions
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID() + extension;

        // Chemin complet du fichier
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Copier le fichier sur le disque
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document document = new Document();
        document.setFileName(originalFilename);
        document.setFilePath(filePath.toString());
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setUploadDate(LocalDateTime.now());
        document.setDossier(dossier);

        return documentRepository.save(document);
    }

    /**
     * Valide un fichier uploadé
     *
     * @param file Fichier à valider
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Vérifier la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier est trop volumineux (max 5 Mo)");
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Formats acceptés : PDF, JPEG, PNG");
        }
    }

    /**
     * Trouve tous les documents d'un dossier
     *
     * @param dossier Dossier parent
     * @return Liste des documents
     */
    public List<Document> findByDossier(Dossier dossier) {
        return documentRepository.findByDossier(dossier);
    }

    /**
     * Compte le nombre de documents d'un dossier
     *
     * @param dossier Dossier parent
     * @return Nombre de documents
     */
    public long countByDossier(Dossier dossier) {
        return documentRepository.countByDossier(dossier);
    }
}
