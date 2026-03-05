package com.mmotors.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        // Vérifier si c'est un changement d'email
        if (Boolean.TRUE.equals(request.getSession().getAttribute("emailChanged"))) {
            model.addAttribute("emailChangedMessage", true);
            request.getSession().removeAttribute("emailChanged");
        }
        return "login";
    }

}
