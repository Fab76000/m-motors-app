package com.mmotors.controller;

import org.springframework.stereotype.Controller;
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
    public String home() {
        return "index";
    }
}
