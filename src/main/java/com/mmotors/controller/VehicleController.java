package com.mmotors.controller;

import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleType;
import com.mmotors.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Contrôleur pour la recherche et l'affichage des véhicules
 */
@Controller
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    /**
     * Page de recherche de véhicules
     * @param type Type de véhicule (ACHAT ou LOCATION)
     * @param brand Marque du véhicule
     * @param maxPrice Prix maximum
     * @param page Numéro de page (défaut: 0)
     * @param model Modèle Spring MVC
     * @return Vue search.html
     */

    @GetMapping("/vehicles")
    public String searchVehicles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        VehicleType vehicleType = null;
        if (type != null && !type.isEmpty()) {
            try {
                vehicleType = VehicleType.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }

        Page<Vehicle> vehiclesPage = vehicleService.searchVehicles(vehicleType, brand, maxPrice, page, 12);

        model.addAttribute("vehicles", vehiclesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vehiclesPage.getTotalPages());
        model.addAttribute("totalItems", vehiclesPage.getTotalElements());

        model.addAttribute("selectedType", type);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedMaxPrice", maxPrice);

        return "search";
    }
}
