package com.mmotors.controller;

import com.mmotors.dto.UserDTO;
import com.mmotors.entity.User;
import com.mmotors.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour l'authentification (inscription, connexion, déconnexion)
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Affiche le formulaire d'inscription
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    /**
     * Traite la soumission du formulaire d'inscription
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Vérifier les erreurs de validation
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Créer l'utilisateur
            User user = userService.createUser(userDTO);

            // Message de succès
            redirectAttributes.addFlashAttribute("successMessage",
                    "Inscription réussie ! Bienvenue " + user.getFirstName() + " !");

            // Redirection vers la page de connexion
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Erreurs métier (email existe, mots de passe différents, etc.)
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        } catch (Exception e) {
            // Erreur inattendue
            model.addAttribute("errorMessage", "Une erreur est survenue lors de l'inscription");
            return "register";
        }
    }
}
