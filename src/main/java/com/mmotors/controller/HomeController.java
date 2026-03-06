package com.mmotors.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour les pages publiques (accueil...)
 */
@Controller
public class HomeController {

    /**
     * Page d'accueil M-Motors
     * Accessible sans authentification
     */

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        // Vérifier si c'est une suppression de compte
        if (Boolean.TRUE.equals(request.getSession().getAttribute("accountDeleted"))) {
            model.addAttribute("accountDeletedMessage", true);
            request.getSession().removeAttribute("accountDeleted");
        }
        return "index";
    }
    
    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Test erreur 500 prod");
    }
}
