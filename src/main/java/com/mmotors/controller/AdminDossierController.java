package com.mmotors.controller;
import com.mmotors.entity.Dossier;
import com.mmotors.entity.DossierStatus;
import com.mmotors.entity.DossierType;
import com.mmotors.service.DossierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller admin pour la gestion des dossiers
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDossierController {

    private final DossierService dossierService;

    /**
     * Page de visualisation de tous les dossiers avec filtres
     */
    @GetMapping("/dossiers")
    public String viewDossiers(
            @RequestParam(required = false) DossierStatus status,
            @RequestParam(required = false) DossierType type,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Dossier> dossiersPage = dossierService.searchDossiersWithDetails(status, type, pageable);
        model.addAttribute("dossiers", dossiersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", dossiersPage.getTotalPages());
        model.addAttribute("totalItems", dossiersPage.getTotalElements());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("statuses", DossierStatus.values());
        model.addAttribute("types", DossierType.values());

        return "admin/dossiers";
    }

    /**
     * Valider un dossier
     */
    @PostMapping("/dossiers/{dossierId}/validate")
    public String validateDossier(
            @PathVariable Long dossierId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Dossier dossier = dossierService.validateDossier(dossierId);

            // TODO : Envoyer email de notification au client (pour version future)

            redirectAttributes.addFlashAttribute("successMessage",
                    "Dossier " + dossier.getReferenceNumber() + " validé avec succès !");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/dossiers";
    }

    /**
     * Rejeter un dossier
     */
    @PostMapping("/dossiers/{dossierId}/reject")
    public String rejectDossier(
            @PathVariable Long dossierId,
            @RequestParam String rejectionReason,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Dossier dossier = dossierService.rejectDossier(dossierId, rejectionReason);

            // TODO : Envoyer email de notification au client (pour version future)

            redirectAttributes.addFlashAttribute("successMessage",
                    "Dossier " + dossier.getReferenceNumber() + " rejeté.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/dossiers";
    }
}