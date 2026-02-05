package com.mmotors.service;
import com.mmotors.entity.*;
import com.mmotors.repository.DossierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour DossierService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DossierService Tests")
public class DossierServiceTest {

    @Mock
    private DossierRepository dossierRepository;

    @InjectMocks
    private DossierService dossierService;

    private User testUser;
    private Vehicle testVehicleAchat;
    private Vehicle testVehicleLocation;
    private Dossier testDossierAchat;
    private Dossier testDossierLocation;

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

    @Test
    @DisplayName("findById - Dossier trouvé")
    void findById_DossierExists_ReturnsDossier() {
        when(dossierRepository.findById(1L)).thenReturn(Optional.of(testDossierAchat));

        Dossier result = dossierService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReferenceNumber()).isEqualTo("DOSS-2026-00001");
        verify(dossierRepository).findById(1L);
    }

    @Test
    @DisplayName("findById - Dossier non trouvé")
    void findById_DossierNotFound_ThrowsException() {
        when(dossierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dossierService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dossier non trouvé");

        verify(dossierRepository).findById(999L);
    }

    // ==================== TESTS findByReferenceNumber ====================

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

    @Test
    @DisplayName("findByUser - Retourne les dossiers de l'utilisateur")
    void findByUser_ReturnsUserDossiers() {
        List<Dossier> dossiers = Arrays.asList(testDossierAchat, testDossierLocation);
        when(dossierRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(dossiers);


        List<Dossier> result = dossierService.findByUser(testUser);


        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testDossierAchat, testDossierLocation);
        verify(dossierRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("findByUser - Aucun dossier pour l'utilisateur")
    void findByUser_NoDossiers_ReturnsEmptyList() {
        when(dossierRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(List.of());

        List<Dossier> result = dossierService.findByUser(testUser);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(dossierRepository).findByUserOrderByCreatedAtDesc(testUser);
    }
}