package com.mmotors.service;

import com.mmotors.entity.*;
import com.mmotors.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour DocumentService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentService Tests")
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    private Dossier testDossier;
    private Document testDocument;
    private MultipartFile validFile;
    private MultipartFile invalidFile;
    private MultipartFile oversizedFile;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        Vehicle testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setBrand("Geupeot");
        testVehicle.setModel("803");
        testVehicle.setType(VehicleType.ACHAT);
        testVehicle.setPrice(new BigDecimal("18500.00"));

        testDossier = new Dossier();
        testDossier.setId(1L);
        testDossier.setReferenceNumber("DOSS-2026-00001");
        testDossier.setType(DossierType.ACHAT);
        testDossier.setStatus(DossierStatus.EN_COURS);
        testDossier.setUser(testUser);
        testDossier.setVehicle(testVehicle);
        testDossier.setCreatedAt(LocalDateTime.now());

        testDocument = new Document();
        testDocument.setId(1L);
        testDocument.setFileName("piece_identite.pdf");
        testDocument.setFilePath("/uploads/documents/DOSS-2026-00001/piece_identite.pdf");
        testDocument.setFileType(DocumentType.PIECE_ID);
        testDocument.setFileSize(1024000L); // 1 MB
        testDocument.setDossier(testDossier);
        testDocument.setUploadDate(LocalDateTime.now());

        validFile = new MockMultipartFile(
                "file",
                "piece_identite.pdf",
                "application/pdf",
                new byte[1024 * 1024] // 1 MB
        );

        invalidFile = new MockMultipartFile(
                "file",
                "malware.exe",
                "application/octet-stream",
                new byte[1024]
        );

        oversizedFile = new MockMultipartFile(
                "file",
                "huge_file.pdf",
                "application/pdf",
                new byte[6 * 1024 * 1024] // 6 MB (> 5 MB limit)
        );
    }

    // ==================== TESTS saveDocument ====================

    /**
     * Save document valid pdf file saves successfully.
     */
    @Test
    @DisplayName("saveDocument - Upload fichier PDF valide")
    void saveDocument_ValidPdfFile_SavesSuccessfully() {
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        Document result = null;
        try {
            result = documentService.saveDocument(validFile, DocumentType.PIECE_ID, testDossier);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo("piece_identite.pdf");
        assertThat(result.getFileType()).isEqualTo(DocumentType.PIECE_ID);
        assertThat(result.getDossier()).isEqualTo(testDossier);
        verify(documentRepository).save(any(Document.class));
    }

    /**
     * Save document empty file throws exception.
     */
    @Test
    @DisplayName("saveDocument - Fichier vide rejeté")
    void saveDocument_EmptyFile_ThrowsException() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThatThrownBy(() -> documentService.saveDocument(emptyFile, DocumentType.PIECE_ID, testDossier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vide");

        verify(documentRepository, never()).save(any(Document.class));
    }

    /**
     * Save document oversized file throws exception.
     */
    @Test
    @DisplayName("saveDocument - Fichier trop volumineux rejeté (> 5 MB)")
    void saveDocument_OversizedFile_ThrowsException() {
        assertThatThrownBy(() -> documentService.saveDocument(oversizedFile, DocumentType.PIECE_ID, testDossier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 Mo");

        verify(documentRepository, never()).save(any(Document.class));
    }

    /**
     * Save document invalid file type throws exception.
     */
    @Test
    @DisplayName("saveDocument - Type de fichier non autorisé rejeté")
    void saveDocument_InvalidFileType_ThrowsException() {
        assertThatThrownBy(() -> documentService.saveDocument(invalidFile, DocumentType.PIECE_ID, testDossier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Type de fichier non autorisé");

        verify(documentRepository, never()).save(any(Document.class));
    }

    /**
     * Save document valid jpg file saves successfully.
     */
    @Test
    @DisplayName("saveDocument - Upload fichier image JPG valide")
    void saveDocument_ValidJpgFile_SavesSuccessfully() {
        MultipartFile jpgFile = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                new byte[1024 * 500] // 500 KB
        );

        Document jpgDocument = new Document();
        jpgDocument.setId(2L);
        jpgDocument.setFileName("photo.jpg");
        jpgDocument.setFileType(DocumentType.PIECE_ID);

        when(documentRepository.save(any(Document.class))).thenReturn(jpgDocument);

        Document result = null;
        try {
            result = documentService.saveDocument(jpgFile, DocumentType.PIECE_ID, testDossier);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo("photo.jpg");
        verify(documentRepository).save(any(Document.class));
    }

    // ==================== TESTS findByDossier ====================

    /**
     * Find by dossier returns dossier documents.
     */
    @Test
    @DisplayName("findByDossier - Retourne les documents du dossier")
    void findByDossier_ReturnsDossierDocuments() {

        Document doc1 = new Document();
        doc1.setId(1L);
        doc1.setFileType(DocumentType.PIECE_ID);
        doc1.setDossier(testDossier);

        Document doc2 = new Document();
        doc2.setId(2L);
        doc2.setFileType(DocumentType.JUSTIFICATIF_DOMICILE);
        doc2.setDossier(testDossier);

        List<Document> documents = Arrays.asList(doc1, doc2);
        when(documentRepository.findByDossier(testDossier)).thenReturn(documents);

        List<Document> result = documentService.findByDossier(testDossier);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(doc1, doc2);
        verify(documentRepository).findByDossier(testDossier);
    }

    /**
     * Find by dossier no documents returns empty list.
     */
    @Test
    @DisplayName("findByDossier - Aucun document pour le dossier")
    void findByDossier_NoDocuments_ReturnsEmptyList() {

        when(documentRepository.findByDossier(testDossier)).thenReturn(Arrays.asList());

        List<Document> result = documentService.findByDossier(testDossier);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(documentRepository).findByDossier(testDossier);
    }

    // ==================== TESTS countByDossier ====================

    /**
     * Count by dossier returns document count.
     */
    @Test
    @DisplayName("countByDossier - Retourne le nombre de documents")
    void countByDossier_ReturnsDocumentCount() {

        when(documentRepository.countByDossier(testDossier)).thenReturn(3L);

        long result = documentService.countByDossier(testDossier);

        assertThat(result).isEqualTo(3L);
        verify(documentRepository).countByDossier(testDossier);
    }

    /**
     * Count by dossier no documents returns zero.
     */
    @Test
    @DisplayName("countByDossier - Aucun document retourne zéro")
    void countByDossier_NoDocuments_ReturnsZero() {
        when(documentRepository.countByDossier(testDossier)).thenReturn(0L);

        long result = documentService.countByDossier(testDossier);

        assertThat(result).isEqualTo(0L);
        verify(documentRepository).countByDossier(testDossier);
    }
}