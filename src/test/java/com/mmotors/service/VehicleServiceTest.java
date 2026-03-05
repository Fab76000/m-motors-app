package com.mmotors.service;

import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleStatus;
import com.mmotors.entity.VehicleType;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The type Vehicle service test.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DossierRepository dossierRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle1;
    private Vehicle testVehicle2;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        // Véhicule à vendre
        testVehicle1 = new Vehicle();
        testVehicle1.setId(1L);
        testVehicle1.setBrand("Geupeot");
        testVehicle1.setModel("803");
        testVehicle1.setYear(2023);
        testVehicle1.setMileage(15000);
        testVehicle1.setType(VehicleType.ACHAT);
        testVehicle1.setPrice(new BigDecimal("18500.00"));
        testVehicle1.setStatus(VehicleStatus.DISPONIBLE);
        testVehicle1.setColor("Blanc");
        testVehicle1.setFuelType("Essence");
        testVehicle1.setPower(130);
        testVehicle1.setGearbox("Manuelle");
        testVehicle1.setDoors(5);

        // Véhicule en location
        testVehicle2 = new Vehicle();
        testVehicle2.setId(2L);
        testVehicle2.setBrand("Nerault");
        testVehicle2.setModel("Olic");
        testVehicle2.setYear(2024);
        testVehicle2.setMileage(5000);
        testVehicle2.setType(VehicleType.LOCATION);
        testVehicle2.setMonthlyRent(new BigDecimal("450.00"));
        testVehicle2.setStatus(VehicleStatus.DISPONIBLE);
        testVehicle2.setColor("Gris");
        testVehicle2.setFuelType("Hybride");
        testVehicle2.setPower(140);
        testVehicle2.setGearbox("Automatique");
        testVehicle2.setDoors(5);
    }

    // ==================== TESTS searchVehicles ====================

    /**
     * Search vehicles with filters returns filtered page.
     */
    @Test
    @DisplayName("searchVehicles - Avec filtres (type, brand, maxPrice)")
    void searchVehicles_WithFilters_ReturnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("id").descending());
        List<Vehicle> vehicles = Collections.singletonList(testVehicle1);
        Page<Vehicle> expectedPage = new PageImpl<>(vehicles, pageable, 1);

        when(vehicleRepository.searchVehicles(
                VehicleType.ACHAT,
                "%GEUPEOT%",
                new BigDecimal("20000"),
                VehicleStatus.DISPONIBLE,
                pageable
        )).thenReturn(expectedPage);

        Page<Vehicle> result = vehicleService.searchVehicles(
                VehicleType.ACHAT,
                "Geupeot",
                new BigDecimal("20000"),
                0,
                12
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Geupeot");
        verify(vehicleRepository).searchVehicles(
                VehicleType.ACHAT,
                "%GEUPEOT%",
                new BigDecimal("20000"),
                VehicleStatus.DISPONIBLE,
                pageable
        );
    }

    /**
     * Search vehicles without filters returns all vehicles.
     */
    @Test
    @DisplayName("searchVehicles - Sans filtres (tous les véhicules)")
    void searchVehicles_WithoutFilters_ReturnsAllVehicles() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("id").descending());
        List<Vehicle> vehicles = Arrays.asList(testVehicle1, testVehicle2);
        Page<Vehicle> expectedPage = new PageImpl<>(vehicles, pageable, 2);

        when(vehicleRepository.searchVehicles(
                null,
                null,
                null,
                VehicleStatus.DISPONIBLE,
                pageable
        )).thenReturn(expectedPage);

        Page<Vehicle> result = vehicleService.searchVehicles(null, null, null, 0, 12);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(vehicleRepository).searchVehicles(
                null,
                null,
                null,
                VehicleStatus.DISPONIBLE,
                pageable
        );
    }
    // ==================== TESTS findById ====================

    /**
     * Find by id vehicle exists returns vehicle.
     */
    @Test
    @DisplayName("findById - Véhicule trouvé")
    void findById_VehicleExists_ReturnsVehicle() {

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle1));


        Vehicle result = vehicleService.findById(1L);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBrand()).isEqualTo("Geupeot");
        verify(vehicleRepository).findById(1L);
    }

    /**
     * Find by id vehicle not found throws exception.
     */
    @Test
    @DisplayName("findById - Véhicule non trouvé")
    void findById_VehicleNotFound_ThrowsException() {

        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Véhicule non trouvé");

        verify(vehicleRepository).findById(999L);
    }

    // ==================== TESTS count ====================

    /**
     * Count returns total.
     */
    @Test
    @DisplayName("count - Retourne le nombre total de véhicules")
    void count_ReturnsTotal() {

        when(vehicleRepository.count()).thenReturn(25L);


        long result = vehicleService.count();


        assertThat(result).isEqualTo(25L);
        verify(vehicleRepository).count();
    }

    // ==================== TESTS countByStatus ====================

    /**
     * Count by status returns count for status.
     */
    @Test
    @DisplayName("countByStatus - Compte les véhicules disponibles")
    void countByStatus_ReturnsCountForStatus() {

        when(vehicleRepository.countByStatus(VehicleStatus.DISPONIBLE)).thenReturn(15L);


        long result = vehicleService.countByStatus(VehicleStatus.DISPONIBLE);


        assertThat(result).isEqualTo(15L);
        verify(vehicleRepository).countByStatus(VehicleStatus.DISPONIBLE);
    }

    // ==================== TESTS save ====================

    /**
     * Save new vehicle saves successfully.
     */
    @Test
    @DisplayName("save - Création d'un nouveau véhicule")
    void save_NewVehicle_SavesSuccessfully() {

        Vehicle newVehicle = new Vehicle();
        newVehicle.setBrand("Tuyutu");
        newVehicle.setModel("Siary");
        newVehicle.setType(VehicleType.ACHAT);
        newVehicle.setPrice(new BigDecimal("15000"));

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(newVehicle);


        Vehicle result = vehicleService.save(newVehicle);


        assertThat(result).isNotNull();
        assertThat(result.getBrand()).isEqualTo("Tuyutu");
        verify(vehicleRepository).save(newVehicle);
    }

    /**
     * Save existing vehicle updates successfully.
     */
    @Test
    @DisplayName("save - Mise à jour d'un véhicule existant")
    void save_ExistingVehicle_UpdatesSuccessfully() {

        testVehicle1.setPrice(new BigDecimal("17000")); // Modification du prix

        when(vehicleRepository.save(testVehicle1)).thenReturn(testVehicle1);


        Vehicle result = vehicleService.save(testVehicle1);


        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("17000"));
        verify(vehicleRepository).save(testVehicle1);
    }

    // ==================== TESTS findAll ====================

    /**
     * Find all returns all vehicles.
     */
    @Test
    @DisplayName("findAll - Retourne tous les véhicules")
    void findAll_ReturnsAllVehicles() {

        List<Vehicle> vehicles = Arrays.asList(testVehicle1, testVehicle2);
        when(vehicleRepository.findAll()).thenReturn(vehicles);


        List<Vehicle> result = vehicleService.findAll();


        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testVehicle1, testVehicle2);
        verify(vehicleRepository).findAll();
    }

    // ==================== TESTS updateVehicle ====================

    /**
     * Test update vehicle success.
     */
    @Test
    void testUpdateVehicle_Success() {
        Long vehicleId = 1L;

        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(vehicleId);
        existingVehicle.setBrand("Geupeot");
        existingVehicle.setModel("803");
        existingVehicle.setPrice(new BigDecimal("18500"));

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setBrand("Nerault");
        updatedVehicle.setModel("Olic");
        updatedVehicle.setPrice(new BigDecimal("15000"));
        updatedVehicle.setType(VehicleType.ACHAT);
        updatedVehicle.setYear(2023);
        updatedVehicle.setMileage(25000);
        updatedVehicle.setFuelType("Essence");
        updatedVehicle.setPower(110);
        updatedVehicle.setGearbox("Manuelle");
        updatedVehicle.setDoors(5);
        updatedVehicle.setColor("Blanc");
        updatedVehicle.setDescription("Excellent état");
        updatedVehicle.setStatus(VehicleStatus.DISPONIBLE);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(dossierRepository.countByVehicleIdAndStatusIn(eq(vehicleId), anyList())).thenReturn(0L);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(existingVehicle);

        Vehicle result = vehicleService.updateVehicle(vehicleId, updatedVehicle);

        assertNotNull(result);
        assertEquals("Nerault", result.getBrand());
        assertEquals("Olic", result.getModel());
        verify(vehicleRepository).save(existingVehicle);
    }

    /**
     * Test update vehicle blocked by active dossiers.
     */
    @Test
    void testUpdateVehicle_BlockedByActiveDossiers() {

        Long vehicleId = 1L;
        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(vehicleId);

        Vehicle updatedVehicle = new Vehicle();

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(dossierRepository.countByVehicleIdAndStatusIn(eq(vehicleId), anyList())).thenReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.updateVehicle(vehicleId, updatedVehicle);
        });
    }

    /**
     * Test delete vehicle success.
     */
    @Test
    void testDeleteVehicle_Success() {
        Long vehicleId = 1L;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setBrand("Geupeot");
        vehicle.setModel("803");

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        doNothing().when(vehicleRepository).delete(vehicle);

        vehicleService.deleteVehicle(vehicleId);

        verify(vehicleRepository).delete(vehicle);
    }

    /**
     * Test delete vehicle blocked by dossiers.
     */
    @Test
    void testDeleteVehicle_BlockedByDossiers() {
        Long vehicleId = 1L;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        doThrow(new RuntimeException("FK constraint")).when(vehicleRepository).delete(vehicle);

        assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.deleteVehicle(vehicleId);
        });
    }
}
