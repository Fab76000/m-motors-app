package com.mmotors.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour le formulaire d'inscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 30, message = "Le nom ne peut pas dépasser 30 caractères")
    private String lastName;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 20, message = "Le prénom ne peut pas dépasser 20 caractères")
    private String firstName;


    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{12,}$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&#)"
    )
    private String password;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;

    @NotNull(message = "Vous devez accepter les conditions RGPD")
    private Boolean rgpdConsent;
}
