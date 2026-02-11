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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller admin pour la gestion des dossiers
 */
@Controller
@RequiredArgsConstructor
public class AdminDossierController {

    private final DossierService dossierService;

    /**
     * Page de visualisation de tous les dossiers avec filtres
     */
    @GetMapping("/admin/dossiers")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewDossiers(
            @RequestParam(required = false) DossierStatus status,
            @RequestParam(required = false) DossierType type,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // Pagination : 10 dossiers par page, triés par date de création décroissante
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<Dossier> dossiersPage = dossierService.searchDossiers(status, type, pageable);

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
}