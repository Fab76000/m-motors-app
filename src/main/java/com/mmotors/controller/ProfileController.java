package com.mmotors.controller;

import com.mmotors.entity.User;
import com.mmotors.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    /**
     * Page profil de l'utilisateur (après connexion)
     */

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Modifier les informations personnelles
     */
    @PostMapping("/profile/update-info")
    public String updateInfo(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            String oldEmail = user.getEmail();

            userService.updateUserInfo(user.getId(), firstName, lastName, email);

            // Si l'email a changé, invalider la session et déconnecter
            if (!oldEmail.equals(email)) {
                // Stocker un flag avant d'invalider la session
                request.getSession().setAttribute("emailChanged", true);

                // Déconnexion
                SecurityContextHolder.clearContext();

                return "redirect:/login";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Informations mises à jour avec succès");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
    }

    /**
     * Changer le mot de passe
     */
    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            userService.changePassword(user.getId(), oldPassword, newPassword, confirmPassword);

            redirectAttributes.addFlashAttribute("successMessage", "Mot de passe modifié avec succès");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
    }

    /**
     * Exporter les données personnelles (RGPD Article 20)
     */
    @GetMapping("/profile/export-data")
    public ResponseEntity<String> exportData(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String jsonData = String.format("""
            {
              "lastName": "%s",
              "firstName": "%s",
              "email": "%s",
              "role": "%s",
              "rgpdConsent": %b,
              "rgpdConsentDate": "%s",
              "createdAt": "%s"
            }
            """,
                user.getLastName(),
                user.getFirstName(),
                user.getEmail(),
                user.getRole(),
                user.getRgpdConsent(),
                user.getRgpdConsentDate() != null ? user.getRgpdConsentDate().format(formatter) : "N/A",
                user.getCreatedAt().format(formatter)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "mes-donnees-mmotors.json");

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonData);
    }

    /**
     * Supprimer le compte (RGPD Article 17 - Droit à l'oubli)
     */
    @PostMapping("/profile/delete-account")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String password,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            userService.deleteAccount(user.getId(), password);
            // Stocker un flag avant d'invalider la session
            request.getSession().setAttribute("accountDeleted", true);
            // Déconnexion
            SecurityContextHolder.clearContext();
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/profile";
        }
    }

}
