package com.mmotors.service;
import com.mmotors.entity.*;
import com.mmotors.repository.DossierRepository;
import com.mmotors.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The type Dossier service test.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DossierService Tests")
public class DossierServiceTest {

    @Mock
    private DossierRepository dossierRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DossierService dossierService;

    private User testUser;
    private Vehicle testVehicleAchat;
    private Vehicle testVehicleLocation;
    private Dossier testDossierAchat;
    private Dossier testDossierLocation;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        // Utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(Role.USER);

        // Véhicule à vendre
        testVehicleAchat = new Vehicle();
        testVehicleAchat.setId(1L);
        testVehicleAchat.setBrand("Geupeot");
        testVehicleAchat.setModel("803");
        testVehicleAchat.setType(VehicleType.ACHAT);
        testVehicleAchat.setPrice(new BigDecimal("18500.00"));
        testVehicleAchat.setStatus(VehicleStatus.DISPONIBLE);

        // Véhicule en location
        testVehicleLocation = new Vehicle();
        testVehicleLocation.setId(2L);
        testVehicleLocation.setBrand("Nerault");
        testVehicleLocation.setModel("Olic");
        testVehicleLocation.setType(VehicleType.LOCATION);
        testVehicleLocation.setMonthlyRent(new BigDecimal("450.00"));
        testVehicleLocation.setStatus(VehicleStatus.DISPONIBLE);

        // Dossier ACHAT
        testDossierAchat = new Dossier();
        testDossierAchat.setId(1L);
        testDossierAchat.setReferenceNumber("DOSS-2026-00001");
        testDossierAchat.setType(DossierType.ACHAT);
        testDossierAchat.setStatus(DossierStatus.EN_COURS);
        testDossierAchat.setUser(testUser);
        testDossierAchat.setVehicle(testVehicleAchat);
        testDossierAchat.setPaymentMode("Comptant");
        testDossierAchat.setTradeIn(false);
        testDossierAchat.setCreatedAt(LocalDateTime.now());

        // Dossier LOCATION
        testDossierLocation = new Dossier();
        testDossierLocation.setId(2L);
        testDossierLocation.setReferenceNumber("DOSS-2026-00002");
        testDossierLocation.setType(DossierType.LOCATION);
        testDossierLocation.setStatus(DossierStatus.EN_COURS);
        testDossierLocation.setUser(testUser);
        testDossierLocation.setVehicle(testVehicleLocation);
        testDossierLocation.setDuration(36);
        testDossierLocation.setCreatedAt(LocalDateTime.now());
    }

    // ==================== TESTS createDossier ====================

    /**
     * Create dossier type achat saves successfully.
     */
    @Test
    @DisplayName("createDossier - Création dossier ACHAT avec succès")
    void createDossier_TypeAchat_SavesSuccessfully() {
        when(dossierRepository.save(any(Dossier.class))).thenReturn(testDossierAchat);

        Dossier result = dossierService.createDossier(
                testUser,
                testVehicleAchat,
                DossierType.ACHAT,
                "Comptant",
                false,
                null
        );

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(DossierType.ACHAT);
        assertThat(result.getStatus()).isEqualTo(DossierStatus.EN_COURS);
        assertThat(result.getPaymentMode()).isEqualTo("Comptant");
        assertThat(result.getTradeIn()).isFalse();
        verify(dossierRepository).save(any(Dossier.class));
    }

    /**
     * Create dossier type location saves successfully.
     */
    @Test
    @DisplayName("createDossier - Création dossier LOCATION avec succès")
    void createDossier_TypeLocation_SavesSuccessfully() {
        when(dossierRepository.save(any(Dossier.class))).thenReturn(testDossierLocation);

        Dossier result = dossierService.createDossier(
                testUser,
                testVehicleLocation,
                DossierType.LOCATION,
                null,
                null,
                36
        );

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(DossierType.LOCATION);
        assertThat(result.getStatus()).isEqualTo(DossierStatus.EN_COURS);
        assertThat(result.getDuration()).isEqualTo(36);
        verify(dossierRepository).save(any(Dossier.class));
    }

    /**
     * Create dossier generates valid reference number.
     */
    @Test
    @DisplayName("createDossier - Génère un numéro de référence valide")
    void createDossier_GeneratesValidReferenceNumber() {
        when(dossierRepository.save(any(Dossier.class))).thenAnswer(invocation -> {
            Dossier dossier = invocation.getArgument(0);
            dossier.setId(1L);
            return dossier;
        });

        Dossier result = dossierService.createDossier(
                testUser,
                testVehicleAchat,
                DossierType.ACHAT,
                "Comptant",
                false,
                null
        );

        assertThat(result.getReferenceNumber()).isNotNull();
        assertThat(result.getReferenceNumber()).matches("DOSS-\\d{4}-\\d{5}");
        verify(dossierRepository).save(any(Dossier.class));
    }

    // ==================== TESTS findById ====================

    /**
     * Teste que findByIdWithDetails retourne le dossier correct quand il existe
     * @see DossierService#findByIdWithDetails(Long)
     */
    @Test
    @DisplayName("findById - Dossier trouvé")
    void findById_DossierExists_ReturnsDossier() {
        when(dossierRepository.findByIdWithVehicleAndUser(1L)).thenReturn(Optional.of(testDossierAchat));

        Dossier result = dossierService.findByIdWithDetails(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReferenceNumber()).isEqualTo("DOSS-2026-00001");
        verify(dossierRepository).findByIdWithVehicleAndUser(1L);
    }

    /**
     * Teste que findByIdWithDetails lève une IllegalArgumentException quand le dossier n'existe pas
     * @see DossierService#findByIdWithDetails(Long)
     */
    @Test
    @DisplayName("findById - Dossier non trouvé")
    void findById_DossierNotFound_ThrowsException() {
        when(dossierRepository.findByIdWithVehicleAndUser(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dossierService.findByIdWithDetails(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dossier non trouvé");

        verify(dossierRepository).findByIdWithVehicleAndUser(999L);
    }

    // ==================== TESTS findByReferenceNumber ====================

    /**
     * Find by reference number dossier exists returns dossier.
     */
    @Test
    @DisplayName("findByReferenceNumber - Dossier trouvé")
    void findByReferenceNumber_DossierExists_ReturnsDossier() {
        when(dossierRepository.findByReferenceNumber("DOSS-2026-00001"))
                .thenReturn(Optional.of(testDossierAchat));

        Dossier result = dossierService.findByReferenceNumber("DOSS-2026-00001");

        assertThat(result).isNotNull();
        assertThat(result.getReferenceNumber()).isEqualTo("DOSS-2026-00001");
        verify(dossierRepository).findByReferenceNumber("DOSS-2026-00001");
    }

    /**
     * Find by reference number dossier not found throws exception.
     */
    @Test
    @DisplayName("findByReferenceNumber - Dossier non trouvé")
    void findByReferenceNumber_DossierNotFound_ThrowsException() {
        when(dossierRepository.findByReferenceNumber("INVALID"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> dossierService.findByReferenceNumber("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dossier non trouvé");

        verify(dossierRepository).findByReferenceNumber("INVALID");
    }

    // ==================== TESTS findByUser ====================

    /**
     * Find by user returns user dossiers.
     */
    @Test
    @DisplayName("findByUser - Retourne les dossiers de l'utilisateur")
    void findByUser_ReturnsUserDossiers() {
        when(dossierRepository.findByUserWithVehicle(testUser))
                .thenReturn(List.of(testDossierAchat, testDossierLocation));

        List<Dossier> result = dossierService.findByUser(testUser);

        assertThat(result).hasSize(2);
        verify(dossierRepository).findByUserWithVehicle(testUser);
    }

    /**
     * Find by user no dossiers returns empty list.
     */
    @Test
    @DisplayName("findByUser - Aucun dossier retourne liste vide")
    void findByUser_NoDossiers_ReturnsEmptyList() {
        when(dossierRepository.findByUserWithVehicle(testUser))
                .thenReturn(List.of());

        List<Dossier> result = dossierService.findByUser(testUser);

        assertThat(result).isEmpty();
        verify(dossierRepository).findByUserWithVehicle(testUser);
    }

    // ==================== TESTS validateDossier ====================

    /**
     * Test validate dossier success.
     */
    @Test
    void testValidateDossier_Success() {
        Long dossierId = 1L;
        Dossier dossier = new Dossier();
        dossier.setId(dossierId);
        dossier.setReferenceNumber("DOSS-2026-00001");
        dossier.setStatus(DossierStatus.EN_COURS);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setStatus(VehicleStatus.DISPONIBLE);
        dossier.setVehicle(vehicle);

        when(dossierRepository.findById(dossierId)).thenReturn(Optional.of(dossier));
        when(dossierRepository.save(any(Dossier.class))).thenReturn(dossier);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Dossier result = dossierService.validateDossier(dossierId);

        assertNotNull(result);
        assertEquals(DossierStatus.VALIDE, result.getStatus());
        assertEquals(VehicleStatus.RESERVE, vehicle.getStatus());
        verify(dossierRepository).save(dossier);
        verify(vehicleRepository).save(vehicle);
    }

    /**
     * Test validate dossier already validated.
     */
    @Test
    void testValidateDossier_AlreadyValidated() {
        Long dossierId = 1L;
        Dossier dossier = new Dossier();
        dossier.setId(dossierId);
        dossier.setStatus(DossierStatus.VALIDE);

        when(dossierRepository.findById(dossierId)).thenReturn(Optional.of(dossier));

        assertThrows(IllegalArgumentException.class, () -> {
            dossierService.validateDossier(dossierId);
        });
    }

    /**
     * Test reject dossier success.
     */
    @Test
    void testRejectDossier_Success() {
        Long dossierId = 1L;
        String reason = "Documents illisibles";

        Dossier dossier = new Dossier();
        dossier.setId(dossierId);
        dossier.setReferenceNumber("DOSS-2026-00002");
        dossier.setStatus(DossierStatus.EN_COURS);

        when(dossierRepository.findById(dossierId)).thenReturn(Optional.of(dossier));
        when(dossierRepository.save(any(Dossier.class))).thenReturn(dossier);

        Dossier result = dossierService.rejectDossier(dossierId, reason);

        assertNotNull(result);
        assertEquals(DossierStatus.REJETE, result.getStatus());
        assertEquals(reason, result.getRejectionReason());
        verify(dossierRepository).save(dossier);
    }

    /**
     * Test reject dossier no reason.
     */
    @Test
    void testRejectDossier_NoReason() {
        assertThrows(IllegalArgumentException.class, () -> {
            dossierService.rejectDossier(1L, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            dossierService.rejectDossier(1L, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            dossierService.rejectDossier(1L, "   ");
        });
    }

    /**
     *  Test reject dossier already rejected.
     */
    @Test
    @DisplayName("hasDossierActif - retourne true si dossier actif existe")
    void hasDossierActif_ReturnsTrueWhenExists() {
        when(dossierRepository.existsByUserAndVehicleAndStatusIn(
                testUser, testVehicleAchat, List.of(DossierStatus.EN_COURS, DossierStatus.VALIDE)))
                .thenReturn(true);

        boolean result = dossierService.hasDossierActif(testUser, testVehicleAchat);

        assertThat(result).isTrue();
        verify(dossierRepository).existsByUserAndVehicleAndStatusIn(
                testUser, testVehicleAchat, List.of(DossierStatus.EN_COURS, DossierStatus.VALIDE));
    }

    /** Test has dossier actif returns false when not exists. */

    @Test
    @DisplayName("hasDossierActif - retourne false si aucun dossier actif")
    void hasDossierActif_ReturnsFalseWhenNotExists() {
        when(dossierRepository.existsByUserAndVehicleAndStatusIn(
                testUser, testVehicleAchat, List.of(DossierStatus.EN_COURS, DossierStatus.VALIDE)))
                .thenReturn(false);

        boolean result = dossierService.hasDossierActif(testUser, testVehicleAchat);

        assertThat(result).isFalse();
        verify(dossierRepository).existsByUserAndVehicleAndStatusIn(
                testUser, testVehicleAchat, List.of(DossierStatus.EN_COURS, DossierStatus.VALIDE));
    }

    /**
     * Test create dossier throws exception when dossier active exists.
     */
    @Test
    @DisplayName("createDossier - Lève exception si dossier actif existe déjà")
    void createDossier_DossierActifExiste_ThrowsException() {
        when(dossierRepository.existsByUserAndVehicleAndStatusIn(
                testUser, testVehicleAchat, List.of(DossierStatus.EN_COURS, DossierStatus.VALIDE)))
                .thenReturn(true);

        assertThatThrownBy(() -> dossierService.createDossier(
                testUser, testVehicleAchat, DossierType.ACHAT, "Comptant", false, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Vous avez déjà un dossier");
    }

    /**
     * Test anonymize dossiers sets user to null.
     */
    @Test
    @DisplayName("anonymizeDossiers - Anonymise les dossiers de l'utilisateur")
    void anonymizeDossiers_SetsUserToNull() {
        testDossierAchat.setUser(testUser);
        testDossierLocation.setUser(testUser);

        when(dossierRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(List.of(testDossierAchat, testDossierLocation));
        when(dossierRepository.saveAll(anyList()))
                .thenReturn(List.of(testDossierAchat, testDossierLocation));

        dossierService.anonymizeDossiers(testUser);

        assertThat(testDossierAchat.getUser()).isNull();
        assertThat(testDossierLocation.getUser()).isNull();
        verify(dossierRepository).saveAll(anyList());
    }

    /**
     * Test search dossiers with details.
     */
    @Test
    @DisplayName("searchDossiersWithDetails - Sans filtre retourne tous les dossiers")
    void searchDossiersWithDetails_NoFilter_ReturnsAll() {
        when(dossierRepository.findAllWithVehicleAndUser())
                .thenReturn(List.of(testDossierAchat, testDossierLocation));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Dossier> result = dossierService.searchDossiersWithDetails(null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        verify(dossierRepository).findAllWithVehicleAndUser();
    }

    /**
     * Test search dossiers with details by status.
     */
    @Test
    @DisplayName("searchDossiersWithDetails - Filtre par statut")
    void searchDossiersWithDetails_FilterByStatus_ReturnsDossiers() {
        when(dossierRepository.findByStatusWithDetails(DossierStatus.EN_COURS))
                .thenReturn(List.of(testDossierAchat));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Dossier> result = dossierService.searchDossiersWithDetails(DossierStatus.EN_COURS, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(dossierRepository).findByStatusWithDetails(DossierStatus.EN_COURS);
    }

    /**
     * Test search dossiers with details by type.
     */
    @Test
    @DisplayName("searchDossiersWithDetails - Filtre par type")
    void searchDossiersWithDetails_FilterByType_ReturnsDossiers() {
        when(dossierRepository.findByTypeWithDetails(DossierType.ACHAT))
                .thenReturn(List.of(testDossierAchat));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Dossier> result = dossierService.searchDossiersWithDetails(null, DossierType.ACHAT, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(dossierRepository).findByTypeWithDetails(DossierType.ACHAT);
    }

    /**
     * Test search dossiers with details by status and type.
     */
    @Test
    @DisplayName("searchDossiersWithDetails - Filtre par statut ET type")
    void searchDossiersWithDetails_FilterByStatusAndType_ReturnsDossiers() {
        when(dossierRepository.findByStatusAndTypeWithDetails(DossierStatus.EN_COURS, DossierType.ACHAT))
                .thenReturn(List.of(testDossierAchat));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Dossier> result = dossierService.searchDossiersWithDetails(DossierStatus.EN_COURS, DossierType.ACHAT, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(dossierRepository).findByStatusAndTypeWithDetails(DossierStatus.EN_COURS, DossierType.ACHAT);
    }
}