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
     *
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
     *
     * @param email Email de l'utilisateur
     * @return Utilisateur trouvé ou null
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Met à jour les informations personnelles d'un utilisateur
     */

    @Transactional
    public User updateUserInfo(Long userId, String lastName, String firstName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        //Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {

            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return userRepository.save(user);
    }

    /**
     * Change le mot de passe d'un utilisateur
     */

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        //Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

         // Vérifier que les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Les nouveaux mots de passe ne correspondent pas");
        }

        // Vérifier la complexité du nouveau mot de passe (12 caractères minimum)

        if (newPassword.length() < 12) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 12 caractères");
        }

// Vérifier la complexité (majuscule, minuscule, chiffre, caractère spécial)
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{12,}$";
        if (!newPassword.matches(passwordRegex)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&#)");
        }

        //Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Supprimer le compte d'un utilisateur (droit à l'oubli RGPD)
     */
    @Transactional
    public void deleteAccount(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        // Vérifier le mot de passe avant suppression
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        //Supprimer l'utilisateur
        userRepository.delete(user);
    }

}