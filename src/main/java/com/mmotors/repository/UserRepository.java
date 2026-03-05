package com.mmotors.repository;


import com.mmotors.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité User
 * Gère les opérations CRUD sur la table users
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Recherche un utilisateur par son email
     * @param email Email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */

    Optional<User> findByEmail(String email);
    /**
     * Vérifie si un email existe déjà en base
     * @param email Email à vérifier
     * @return true si l'email existe déjà
     */
    boolean existsByEmail(String email);
}
