package com.mmotors.controller;

import com.mmotors.entity.DossierStatus;
import com.mmotors.entity.User;
import com.mmotors.entity.Vehicle;
import com.mmotors.entity.VehicleType;
import com.mmotors.service.DossierService;
import com.mmotors.service.UserService;
import com.mmotors.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur pour la recherche et l'affichage des véhicules
 */
@Controller
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    private final DossierService dossierService;
    private final UserService userService;

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
/**
 * Page de détails d'un véhicule
 * @param id Identifiant du véhicule
 * @param model Modèle Spring MVC
 * @return Vue details.html
 */

    @GetMapping("/vehicles/{id}")
    public String vehicleDetails(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model) {
    try {
        Vehicle vehicle = vehicleService.findById(id);
        model.addAttribute("vehicle", vehicle);

        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername());
            model.addAttribute("dossierActif", dossierService.hasDossierActif(user, vehicle));
        } else {
            model.addAttribute("dossierActif", false);
        }

        return "details";
    } catch (IllegalArgumentException e) {
        return "redirect:/vehicles?error=notfound";
    }
}
    /**
     * Switch un véhicule entre ACHAT et LOCATION
     */
    @PostMapping("/admin/vehicles/{id}/switch")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchVehicleType(
            @PathVariable Long id,
            @RequestParam BigDecimal newPriceOrRent,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Vehicle vehicle = vehicleService.switchVehicleType(id, newPriceOrRent);

            String message = vehicle.getType() == VehicleType.ACHAT
                    ? "Véhicule switché en ACHAT avec succès"
                    : "Véhicule switché en LOCATION avec succès";

            redirectAttributes.addFlashAttribute("success", message);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/vehicles";
    }

}
