package com.mmotors.service;

import com.mmotors.dto.UserDTO;
import com.mmotors.entity.Role;
import com.mmotors.entity.User;
import com.mmotors.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service de gestion des utilisateurs
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur à partir du formulaire d'inscription
     * @param userDTO Données du formulaire
     * @return Utilisateur créé
     * @throws IllegalArgumentException Si l'email existe déjà ou si les mots de passe ne correspondent pas
     */
    @Transactional
    public User createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        if (userDTO.getRgpdConsent() == null || !userDTO.getRgpdConsent()) {
            throw new IllegalArgumentException("Vous devez accepter les conditions RGPD");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());

        // Hacher le mot de passe avec BCrypt
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user.setRole(Role.USER);

        user.setRgpdConsent(true);
        user.setRgpdConsentDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Recherche un utilisateur par son email
     * @param email Email de l'utilisateur
     * @return Utilisateur trouvé ou null
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}