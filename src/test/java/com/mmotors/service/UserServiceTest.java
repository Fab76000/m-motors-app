package com.mmotors.service;

import com.mmotors.dto.UserDTO;
import com.mmotors.entity.Role;
import com.mmotors.entity.User;
import com.mmotors.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.ArgumentMatchers.eq;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("$2a$12$hashedPassword");
        testUser.setRole(Role.USER);
        testUser.setRgpdConsent(true);
        testUser.setRgpdConsentDate(LocalDateTime.now());
        testUser.setCreatedAt(LocalDateTime.now());

        testUserDTO = new UserDTO();
        testUserDTO.setFirstName("John");
        testUserDTO.setLastName("Doe");
        testUserDTO.setEmail("john.doe@example.com");
        testUserDTO.setPassword("Password@123456");
        testUserDTO.setConfirmPassword("Password@123456");
        testUserDTO.setRgpdConsent(true);
    }


    @Test
    @DisplayName("findByEmail - Utilisateur trouvé")
    void findByEmail_UserExists_ReturnsUser() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail("john.doe@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("findByEmail - Utilisateur non trouvé")
    void findByEmail_UserNotFound_ReturnsNull() {
        // Given
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        // When
        User result = userService.findByEmail("unknown@example.com");

        // Then
        assertThat(result).isNull();
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    @DisplayName("createUser - Création réussie")
    void createUser_ValidData_SavesUser() {
        // Given
        String encodedPassword = "$2a$12$encodedPassword";

        when(userRepository.existsByEmail(testUserDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUserDTO.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(testUserDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        verify(passwordEncoder, times(1)).encode(testUserDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - Email déjà utilisé")
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(testUserDTO.getEmail())).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.createUser(testUserDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà utilisé");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - Mots de passe ne correspondent pas")
    void createUser_PasswordMismatch_ThrowsException() {
        // Given
        testUserDTO.setConfirmPassword("DifferentPassword@123456");

        // When / Then
        assertThatThrownBy(() -> userService.createUser(testUserDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("correspondent pas");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - Consentement RGPD manquant")
    void createUser_NoRgpdConsent_ThrowsException() {
        // Given
        testUserDTO.setRgpdConsent(false);

        // When / Then
        assertThatThrownBy(() -> userService.createUser(testUserDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RGPD");

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("updateUserInfo - Email déjà pris par un autre utilisateur")
    void updateUserInfo_EmailTakenByAnotherUser_ThrowsException() {
        // Given
        Long userId = 1L;
        String newEmail = "taken@example.com";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.updateUserInfo(userId, "Smith", "Jane", newEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà utilisé");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail(newEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateUserInfo - Mise à jour réussie")
    void updateUserInfo_ValidData_UpdatesUser() {
        // Given
        Long userId = 1L;
        String newEmail = "newemail@example.com";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUserInfo(userId, "Smith", "Jane", newEmail); // ✅ Ordre: lastName, firstName, email

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail(newEmail);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword - Succès du changement")
    void testChangePasswordSuccess() {
        // Given
        Long userId = 1L;
        String oldPassword = "OldPassword@123456";
        String newPassword = "NewPassword@123456";
        String oldHashedPassword = testUser.getPassword();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, oldHashedPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(userId, oldPassword, newPassword, newPassword);

        // Then
        verify(passwordEncoder).matches(eq(oldPassword), eq(oldHashedPassword));
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword - Ancien mot de passe incorrect")
    void changePassword_WrongOldPassword_ThrowsException() {
        // Given
        Long userId = 1L;
        String wrongOldPassword = "WrongPassword@123456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongOldPassword, testUser.getPassword())).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> userService.changePassword(
                userId, wrongOldPassword, "NewPassword@123456", "NewPassword@123456"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("incorrect");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword - Confirmation ne correspond pas")
    void changePassword_PasswordMismatch_ThrowsException() {
        // Given
        Long userId = 1L;
        String oldPassword = "OldPassword@123456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPassword())).thenReturn(true);
        // When / Then
        assertThatThrownBy(() -> userService.changePassword(
                userId, oldPassword, "NewPassword@123456", "DifferentPassword@123456"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("correspondent pas");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deleteAccount - Suppression réussie")
    void deleteAccount_ValidPassword_DeletesUser() {
        // Given
        Long userId = 1L;
        String password = "Password@123456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        doNothing().when(userRepository).delete(testUser);

        // When
        userService.deleteAccount(userId, password);

        // Then
        verify(passwordEncoder, times(1)).matches(password, testUser.getPassword());
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("deleteAccount - Mot de passe incorrect")
    void deleteAccount_WrongPassword_ThrowsException() {
        // Given
        Long userId = 1L;
        String wrongPassword = "WrongPassword@123456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, testUser.getPassword())).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> userService.deleteAccount(userId, wrongPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("incorrect");

        verify(userRepository, never()).delete(any(User.class));
    }
}