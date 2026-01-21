package com.mmotors.config;

import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleStatus;
import com.mmotors.entity.VehicleType;
import com.mmotors.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Chargement des données de test au démarrage
 */
@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final VehicleRepository vehicleRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            if (vehicleRepository.count() == 0) {
                loadVehicles();
            }
        };
    }
    private void loadVehicles() {
        // Véhicules à vendre
        vehicleRepository.save(createVehicle(
            "Geupeot", "802", 2022, 15000, "Essence", 110, "Manuelle", 5, "Blanc",
            new BigDecimal("18500"), null, VehicleType.ACHAT,
            "GeuPeot 802 en excellent état, première main, entretien suivi.",
            "['/images/vehicles/geupeot-802.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Nerault", "Olic", 2021, 25000, "Diesel", 90, "Manuelle", 5, "Gris",
            new BigDecimal("16000"), null, VehicleType.ACHAT,
            "Neraul Olic économique, faible consommation.",
            "['/images/vehicles/nerault-olic.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Troën", "P3", 2023, 8000, "Essence", 82, "Manuelle", 5, "Rouge",
            new BigDecimal("17500"), null, VehicleType.ACHAT,
            "Troën P3 récente, garantie constructeur.",
            "['/images/vehicles/troen-p3.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Lovksgawen", "Folg", 2020, 45000, "Diesel", 115, "Automatique", 5, "Noir",
            new BigDecimal("22000"), null, VehicleType.ACHAT,
            "Lovksgawen Folg confortable, boîte automatique.",
            "['/images/vehicles/lovksgawen-folg.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "KNJ", "Série 44", 2022, 20000, "Diesel", 190, "Automatique", 4, "Bleu",
            new BigDecimal("38000"), null, VehicleType.ACHAT,
            "KNJ Série 44 sportive, full options.",
            "['/images/vehicles/knj-serie44.jpg']"
        ));

        // Véhicules en location
        vehicleRepository.save(createVehicle(
            "Tespala", "Model 8", 2023, 5000, "Électrique", 283, "Automatique", 4, "Blanc",
            null, new BigDecimal("450"), VehicleType.LOCATION,
            "Tespala Model 8 électrique, autonomie 500km. Location longue durée avec assurance tous risques.",
            "['/images/vehicles/tespala-model8.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Geupeot", "8003", 2023, 10000, "Hybride", 225, "Automatique", 5, "Gris",
            null, new BigDecimal("380"), VehicleType.LOCATION,
            "Geupeot 8003 hybride rechargeable. Location LLD tout compris.",
            "['/images/vehicles/geupeot-8003.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Nerault", "Raptur", 2022, 18000, "Essence", 140, "Automatique", 5, "Orange",
            null, new BigDecimal("320"), VehicleType.LOCATION,
            "Nerault Raptur SUV urbain. Entretien et assistance inclus.",
            "['/images/vehicles/nerault-raptur.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Idau", "C17", 2023, 12000, "Essence", 150, "Automatique", 4, "Noir",
            null, new BigDecimal("420"), VehicleType.LOCATION,
            "Idau C17 premium. Location avec option d'achat.",
            "['/images/vehicles/idau-c17.jpg']"
        ));

        vehicleRepository.save(createVehicle(
            "Cataleya", "Classe Z", 2023, 8000, "Diesel", 136, "Automatique", 5, "Argent",
            null, new BigDecimal("480"), VehicleType.LOCATION,
            "Cataleya Classe Z élégante. Pack premium inclus.",
            "['/images/vehicles/cataleya-classeZ.jpg']"
        ));

        System.out.println("✅ 10 véhicules de test chargés en BDD");
    }

    private Vehicle createVehicle(String brand, String model, int year, int mileage,
                                  String fuelType, int power, String gearbox, int doors,
                                  String color, BigDecimal price, BigDecimal monthlyRent,
                                  VehicleType type, String description, String photoPaths) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setMileage(mileage);
        vehicle.setFuelType(fuelType);
        vehicle.setPower(power);
        vehicle.setGearbox(gearbox);
        vehicle.setDoors(doors);
        vehicle.setColor(color);
        vehicle.setPrice(price);
        vehicle.setMonthlyRent(monthlyRent);
        vehicle.setType(type);
        vehicle.setStatus(VehicleStatus.DISPONIBLE);
        vehicle.setDescription(description);
        vehicle.setPhotoPaths(photoPaths);
        return vehicle;
    }
}



