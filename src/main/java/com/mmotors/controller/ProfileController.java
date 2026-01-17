package com.mmotors.controller;

import com.mmotors.entity.User;
import com.mmotors.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
