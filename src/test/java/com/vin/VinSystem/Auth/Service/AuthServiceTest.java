package com.vin.VinSystem.Auth.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vin.VinSystem.Auth.DTO.ChangePasswordRequest;
import com.vin.VinSystem.Auth.DTO.LoginRequest;
import com.vin.VinSystem.Auth.DTO.LoginResponse;
import com.vin.VinSystem.Auth.DTO.RegisterRequest;
import com.vin.VinSystem.Auth.DTO.UserResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository     userRepository;
    @Mock RoleRepository     roleRepository;
    @Mock UserRoleRepository userRoleRepository;
    @Mock StaffRepository    staffRepository;
    @Mock PasswordEncoder    passwordEncoder;
    @Mock JwtUtil            jwtUtil;
    @Mock MailService        mailService;

    @InjectMocks AuthService authService;

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User mockUser() {
        User u = new User();
        u.setUserId(1L);
        u.setUsername("testuser");
        u.setEmail("test@example.com");
        u.setPassword("encoded");
        u.setName("Test User");
        return u;
    }

    private UserRole mockUserRole(User user, String roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        UserRole ur = new UserRole();
        ur.setUser(user);
        ur.setRole(role);
        return ur;
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setEmail("test@example.com");
        req.setPassword("Password1");
        req.setName("Test User");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(mockUser());
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(new Role()));

        UserResponse res = authService.register(req);

        assertThat(res.getUsername()).isEqualTo("testuser");
    }

    @Test
    void register_fail_duplicateUsername() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setEmail("test@example.com");
        req.setPassword("Password1");
        req.setName("Test User");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Username already exists");
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    void login_success() {
        User user = mockUser();
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("Password1");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1", "encoded")).thenReturn(true);
        when(userRoleRepository.findByUser(user)).thenReturn(List.of(mockUserRole(user, "CUSTOMER")));
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        LoginResponse res = authService.login(req);

        assertThat(res.getToken()).isEqualTo("jwt-token");
        assertThat(res.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void login_fail_wrongPassword() {
        User user = mockUser();
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("WrongPass1");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass1", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("không đúng");
    }

    // ── Change Password ───────────────────────────────────────────────────────

    @Test
    void changePassword_success() {
        User user = mockUser();
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("OldPass1");
        req.setNewPassword("NewPass1");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass1", "encoded")).thenReturn(true);
        when(passwordEncoder.matches("NewPass1", "encoded")).thenReturn(false);

        assertThatNoException().isThrownBy(() -> authService.changePassword("testuser", req));
        verify(userRepository).save(user);
    }

    // ── Reset Password ────────────────────────────────────────────────────────

    @Test
    void resetPassword_success() {
        User user = mockUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThatNoException().isThrownBy(
            () -> authService.resetPassword("test@example.com", "NewPass1"));
        verify(userRepository).save(user);
    }
}