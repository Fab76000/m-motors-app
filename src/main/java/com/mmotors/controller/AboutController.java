package com.mmotors.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour les pages informatives
 */
@Controller
public class AboutController {

    /**
     * Page "A propos de nous
     * @return Vue about.html
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
