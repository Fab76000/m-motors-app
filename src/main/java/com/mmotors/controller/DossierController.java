package com.mmotors.controller;

import com.mmotors.entity.*;
import com.mmotors.service.DocumentService;
import com.mmotors.service.DossierService;
import com.mmotors.service.UserService;
import com.mmotors.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * Contrôleur pour la gestion des dossiers
 */
@Controller
@RequiredArgsConstructor
public class DossierController {

    private final DossierService dossierService;
    private final DocumentService documentService;
    private final VehicleService vehicleService;
    private final UserService userService;

    /**
     * Affiche le formulaire de dépôt de dossier
     *
     * @param vehicleId ID du véhicule
     * @param model Modèle Spring MVC
     * @return Vue depot-dossier.html
     */
    @GetMapping("/vehicles/{vehicleId}/depot-dossier")
    public String showDepotForm(@PathVariable Long vehicleId, Model model) {
        try {
            Vehicle vehicle = vehicleService.findById(vehicleId);
            if (vehicle.getStatus() != VehicleStatus.DISPONIBLE) {
                return "redirect:/vehicles/" + vehicleId + "?error=unavailable";
            }

            model.addAttribute("vehicle", vehicle);
            return "depot-dossier";
        } catch (IllegalArgumentException e) {
            return "redirect:/vehicles?error=notfound";
        }
    }

    /**
     * Traite la soumission du formulaire de dépôt
     *
     * @param vehicleId          ID du véhicule
     * @param userDetails        Utilisateur connecté
     * @param paymentMode        Mode de paiement (si ACHAT)
     * @param tradeIn            Reprise (si ACHAT)
     * @param duration           Durée (si LOCATION)
     * @param pieceId            Document pièce d'identité
     * @param justifDomicile     Document justificatif domicile
     * @param avisImpot          Document avis d'impôt (optionnel)
     * @param bulletinSalaire    Document bulletin de salaire (optionnel)
     * @param rib                Document RIB
     * @param redirectAttributes Attributs de redirection
     * @return Redirection vers page confirmation
     */
    @PostMapping("/vehicles/{vehicleId}/depot-dossier")
    public String submitDossier(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) Boolean tradeIn,
            @RequestParam(required = false) Integer duration,
            @RequestParam("pieceId") MultipartFile pieceId,
            @RequestParam("justifDomicile") MultipartFile justifDomicile,
            @RequestParam(value = "avisImpot", required = false) MultipartFile avisImpot,
            @RequestParam(value = "bulletinSalaire", required = false) MultipartFile bulletinSalaire,
            @RequestParam("rib") MultipartFile rib,
            RedirectAttributes redirectAttributes) {

        try {
            // Récupérer l'utilisateur et le véhicule
            User user = userService.findByEmail(userDetails.getUsername());
            Vehicle vehicle = vehicleService.findById(vehicleId);

            // Déterminer le type de dossier selon le véhicule
            DossierType type = vehicle.getType() == VehicleType.ACHAT
                    ? DossierType.ACHAT
                    : DossierType.LOCATION;

            // Créer le dossier
            Dossier dossier = dossierService.createDossier(
                    user, vehicle, type, paymentMode, tradeIn, duration
            );

            // Uploader les documents obligatoires
            documentService.saveDocument(pieceId, DocumentType.PIECE_ID, dossier);
            documentService.saveDocument(justifDomicile, DocumentType.JUSTIFICATIF_DOMICILE, dossier);
            documentService.saveDocument(rib, DocumentType.RIB, dossier);

            // Uploader les documents optionnels s'ils sont fournis
            if (avisImpot != null && !avisImpot.isEmpty()) {
                documentService.saveDocument(avisImpot, DocumentType.AVIS_IMPOT, dossier);
            }
            if (bulletinSalaire != null && !bulletinSalaire.isEmpty()) {
                documentService.saveDocument(bulletinSalaire, DocumentType.BULLETIN_SALAIRE, dossier);
            }

            // Redirection vers la page de confirmation
            redirectAttributes.addFlashAttribute("successMessage",
                    "Votre dossier a été déposé avec succès !");
            return "redirect:/dossiers/" + dossier.getId() + "/confirmation";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/vehicles/" + vehicleId + "/depot-dossier";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de l'upload des fichiers. Veuillez réessayer.");
            return "redirect:/vehicles/" + vehicleId + "/depot-dossier";
        }
    }

    /**
     * Page de confirmation après dépôt du dossier
     *
     * @param dossierId   ID du dossier
     * @param userDetails Utilisateur connecté
     * @param model Modèle Spring MVC
     * @return Vue confirmation.html
     */
    @GetMapping("/dossiers/{dossierId}/confirmation")
    public String confirmation(
            @PathVariable Long dossierId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername());

            Dossier dossier = dossierService.findByIdWithDetails(dossierId);

            if (!dossier.getUser().getId().equals(currentUser.getId())) {
                // Tentative d'accès non autorisé → 403 Forbidden
                return "redirect:/profile?error=unauthorized";
            }

            model.addAttribute("dossier", dossier);
            return "confirmation";

        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error=notfound";
        }
    }

    /**
     * Affiche la liste des dossiers de l'utilisateur connecté
     *
     * @param userDetails Utilisateur connecté
     * @param model Modèle Spring MVC
     * @return Vue dossiers.html
     */
    @GetMapping("/dossiers")
    public String myDossiers(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        java.util.List<Dossier> dossiers = dossierService.findByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("dossiers", dossiers);

        return "dossiers";
    }
}
