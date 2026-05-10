package com.vin.VinSystem.Auth.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Auth.DTO.QuickCreateCustomerRequest;
import com.vin.VinSystem.Auth.DTO.QuickCreateCustomerResponse;
import com.vin.VinSystem.Auth.DTO.UserResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;

class StaffCustomerServiceTest {

    UserRepository     userRepository     = mock(UserRepository.class);
    RoleRepository     roleRepository     = mock(RoleRepository.class);
    UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);
    PasswordEncoder    passwordEncoder    = mock(PasswordEncoder.class);
    MailService        mailService        = mock(MailService.class);

    StaffCustomerService service;

    @BeforeEach
    void setUp() {
        service = new StaffCustomerService();
        ReflectionTestUtils.setField(service, "userRepository",     userRepository);
        ReflectionTestUtils.setField(service, "roleRepository",     roleRepository);
        ReflectionTestUtils.setField(service, "userRoleRepository", userRoleRepository);
        ReflectionTestUtils.setField(service, "passwordEncoder",    passwordEncoder);
        ReflectionTestUtils.setField(service, "mailService",        mailService);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User mockUser() {
        User u = new User();
        u.setUserId(1L);
        u.setUsername("nguyen_van_an_5678");
        u.setEmail("an@example.com");
        u.setName("Nguyễn Văn An");
        u.setPhoneNumber("0912345678");
        return u;
    }

    private QuickCreateCustomerRequest validRequest() {
        QuickCreateCustomerRequest req = new QuickCreateCustomerRequest();
        req.setName("Nguyễn Văn An");
        req.setPhone("0912345678");
        req.setEmail("an@example.com");
        return req;
    }

    // ── findByPhone ───────────────────────────────────────────────────────────

    @Test
    void findByPhone_success() {
        User user = mockUser();
        Role role = new Role(); role.setRoleName("CUSTOMER");
        UserRole ur = new UserRole(); ur.setUser(user); ur.setRole(role);

        when(userRepository.findFirstByPhoneNumber("0912345678")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUser(user)).thenReturn(List.of(ur));

        UserResponse res = service.findByPhone("0912345678");

        assertThat(res.getName()).isEqualTo("Nguyễn Văn An");
        assertThat(res.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void findByPhone_fail_notFound() {
        when(userRepository.findFirstByPhoneNumber(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByPhone("0912345678"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void findByPhone_fail_invalidPhone() {
        assertThatThrownBy(() -> service.findByPhone("12345"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("không hợp lệ");
    }

    // ── quickCreate ───────────────────────────────────────────────────────────

    @Test
    void quickCreate_success() {
        User saved = mockUser();

        when(userRepository.findFirstByPhoneNumber("0912345678")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("an@example.com")).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(saved);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(new Role()));

        QuickCreateCustomerResponse res = service.quickCreate(validRequest());

        assertThat(res.getName()).isEqualTo("Nguyễn Văn An");
        assertThat(res.getPhone()).isEqualTo("0912345678");
        verify(userRepository).save(any());
        verify(userRoleRepository).save(any());
    }

    @Test
    void quickCreate_fail_phoneDuplicate() {
        when(userRepository.findFirstByPhoneNumber("0912345678")).thenReturn(Optional.of(mockUser()));

        assertThatThrownBy(() -> service.quickCreate(validRequest()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã có tài khoản");
    }

    @Test
    void quickCreate_fail_emailDuplicate() {
        when(userRepository.findFirstByPhoneNumber(any())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("an@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.quickCreate(validRequest()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email này đã được đăng ký");
    }

    @Test
    void quickCreate_fail_shortName() {
        QuickCreateCustomerRequest req = validRequest();
        req.setName("A");

        assertThatThrownBy(() -> service.quickCreate(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("2 ký tự");
    }
}