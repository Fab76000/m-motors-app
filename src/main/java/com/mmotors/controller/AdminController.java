package com.mmotors.controller;

import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleStatus;
import com.mmotors.entity.VehicleType;
import com.mmotors.repository.VehicleRepository;
import com.mmotors.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur pour le backoffice administrateur
 */

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;

    /**
     * Page d'accueil admin (dashboard)
     *
     * @param model Modèle Spring MVC
     * @return Vue admin-dashboard.html
     */
    @GetMapping
    public String dashboard(Model model) {
        long totalVehicles = vehicleService.count();
        long disponibles = vehicleService.countByStatus(VehicleStatus.DISPONIBLE);
        long reserves = vehicleService.countByStatus(VehicleStatus.RESERVE);
        long vendus = vehicleService.countByStatus(VehicleStatus.VENDU);

        model.addAttribute("totalVehicles", totalVehicles);
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("reserves", reserves);
        model.addAttribute("vendus", vendus);

        return "admin/dashboard";
    }

    /**
     * Formulaire d'ajout de véhocule
     *
     * @return Vue admin-add-vehicle.html
     */

    @GetMapping("/vehicles/add")
    public String showAddVehicleForm() {
        return "admin/add-vehicle";
    }

    /**
     * Traitement de l'ajout de véhicule
     */

    @PostMapping("/vehicles/add")
    public String addVehicle(
            @RequestParam String type,
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam Integer mileage,
            @RequestParam String fuelType,
            @RequestParam Integer power,
            @RequestParam String gearbox,
            @RequestParam Integer doors,
            @RequestParam String color,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal monthlyRent,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        try {
            VehicleType vehicleType = VehicleType.valueOf(type);
            if (vehicleType == VehicleType.ACHAT && (price == null || price.compareTo(BigDecimal.ZERO) <= 0)) {
                throw new IllegalArgumentException("Le prix est obligatoire pour un véhicule à vendre");
            }

            if (vehicleType == VehicleType.LOCATION && (monthlyRent == null || monthlyRent.compareTo(BigDecimal.ZERO) <= 0)) {
                throw new IllegalArgumentException("La mensualité est obligatoire pour un véhicule en location");
            }
            Vehicle vehicle = new Vehicle();
            vehicle.setType(vehicleType);
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setYear(Integer.valueOf(year));
            vehicle.setMileage(mileage);
            vehicle.setFuelType(fuelType);
            vehicle.setPower(power);
            vehicle.setGearbox(gearbox);
            vehicle.setDoors(doors);
            vehicle.setColor(color);
            vehicle.setPrice(price);
            vehicle.setMonthlyRent(monthlyRent);
            vehicle.setDescription(description);
            vehicle.setStatus(VehicleStatus.DISPONIBLE);
            vehicle.setPhotoPaths("[]"); // Photos vides pour l'instant

            // Sauvegarder
            vehicleService.save(vehicle);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Véhicule ajouté avec succès !");
            return "redirect:/admin/vehicles";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/vehicles/add";
        }

    }

        /**
         * Liste des véhicules (admin)
         * @param model Modèle Spring MVC
         * @return Vue admin-vehicles.html
         */
        @GetMapping("/vehicles")
        public String listVehicles(@RequestParam(defaultValue = "0") int page, Model model) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            Page<Vehicle> vehiclesPage = vehicleRepository.findAll(pageable);

            model.addAttribute("vehicles", vehiclesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", vehiclesPage.getTotalPages());
            return "admin/vehicles";
        }
    }
