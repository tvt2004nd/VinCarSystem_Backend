package com.vin.VinSystem.Auth.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Auth.DTO.StaffRequest;
import com.vin.VinSystem.Auth.DTO.StaffResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Repository.BranchRepository;

class StaffServiceTest {

    UserRepository     userRepository     = mock(UserRepository.class);
    StaffRepository    staffRepository    = mock(StaffRepository.class);
    RoleRepository     roleRepository     = mock(RoleRepository.class);
    UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);
    BranchRepository   branchRepository   = mock(BranchRepository.class);
    PasswordEncoder    passwordEncoder    = mock(PasswordEncoder.class);

    StaffService service;

    @BeforeEach
    void setUp() {
        service = new StaffService();
        ReflectionTestUtils.setField(service, "userRepository",     userRepository);
        ReflectionTestUtils.setField(service, "staffRepository",    staffRepository);
        ReflectionTestUtils.setField(service, "roleRepository",     roleRepository);
        ReflectionTestUtils.setField(service, "userRoleRepository", userRoleRepository);
        ReflectionTestUtils.setField(service, "branchRepository",   branchRepository);
        ReflectionTestUtils.setField(service, "passwordEncoder",    passwordEncoder);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User mockUser() {
        User u = new User();
        u.setUserId(1L);
        u.setUsername("staffuser");
        u.setEmail("staff@example.com");
        u.setName("Nguyen Van B");
        return u;
    }

    private Branch mockBranch() {
        Branch b = new Branch();
        b.setBranchId(10L);
        b.setBranchName("Chi nhánh HN");
        return b;
    }

    private Staff mockStaff() {
        Staff s = new Staff();
        s.setUserId(1L);
        s.setUser(mockUser());
        s.setBranch(mockBranch());
        s.setPosition("Nhân viên bán hàng");
        s.setWorkStatus("Đang làm");
        return s;
    }

    private StaffRequest validRequest() {
        StaffRequest req = new StaffRequest();
        req.setName("Nguyen Van B");
        req.setUsername("staffuser");
        req.setEmail("staff@example.com");
        req.setPassword("Pass1234");
        req.setPhoneNumber("0912345678");
        req.setPosition("Nhân viên bán hàng");
        req.setWorkStatus("Đang làm");
        req.setBranchId(10L);
        return req;
    }

    // ── getAllStaff ───────────────────────────────────────────────────────────

    @Test
    void getAllStaff_success() {
        when(staffRepository.findAll()).thenReturn(List.of(mockStaff()));
        when(userRoleRepository.findByUser_UserId(any())).thenReturn(List.of());

        List<StaffResponse> result = service.getAllStaff();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("staffuser");
    }

    // ── getStaffById ──────────────────────────────────────────────────────────

    @Test
    void getStaffById_success() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(mockStaff()));
        when(userRoleRepository.findByUser_UserId(any())).thenReturn(List.of());

        StaffResponse res = service.getStaffById(1L);

        assertThat(res.getName()).isEqualTo("Nguyen Van B");
        assertThat(res.getBranchName()).isEqualTo("Chi nhánh HN");
    }

    @Test
    void getStaffById_fail_notFound() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStaffById(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Staff not found");
    }

    // ── createStaff ───────────────────────────────────────────────────────────

    @Test
    void createStaff_success() {
        when(userRepository.existsByUsername("staffuser")).thenReturn(false);
        when(userRepository.existsByEmail("staff@example.com")).thenReturn(false);
        when(staffRepository.findByBranch_BranchId(10L)).thenReturn(List.of());
        when(userRepository.save(any())).thenReturn(mockUser());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(roleRepository.findByRoleName("STAFF")).thenReturn(Optional.of(new Role()));
        when(branchRepository.findById(10L)).thenReturn(Optional.of(mockBranch()));
        when(staffRepository.save(any())).thenReturn(mockStaff());
        when(userRoleRepository.findByUser_UserId(any())).thenReturn(List.of());

        StaffResponse res = service.createStaff(validRequest());

        assertThat(res.getUsername()).isEqualTo("staffuser");
        verify(staffRepository).save(any());
    }

    @Test
    void createStaff_fail_duplicateUsername() {
        when(userRepository.existsByUsername("staffuser")).thenReturn(true);

        assertThatThrownBy(() -> service.createStaff(validRequest()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Username already exists");
    }

    @Test
    void createStaff_fail_duplicateManager() {
        // Chi nhánh đã có Quản lý chi nhánh
        Staff existingManager = mockStaff();
        existingManager.setUserId(99L);
        existingManager.setPosition("Quản lý chi nhánh");

        StaffRequest req = validRequest();
        req.setPosition("Quản lý chi nhánh");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(staffRepository.findByBranch_BranchId(10L)).thenReturn(List.of(existingManager));
        when(branchRepository.findById(10L)).thenReturn(Optional.of(mockBranch()));

        assertThatThrownBy(() -> service.createStaff(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã có Quản lý chi nhánh");
    }

    // ── updateStaff ───────────────────────────────────────────────────────────

    @Test
    void updateStaff_success() {
        Staff staff = mockStaff();
        when(staffRepository.findByUser_UserId(1L)).thenReturn(Optional.of(staff));
        when(staffRepository.findByBranch_BranchId(10L)).thenReturn(List.of());
        when(branchRepository.findById(10L)).thenReturn(Optional.of(mockBranch()));
        when(userRepository.save(any())).thenReturn(mockUser());
        when(staffRepository.save(any())).thenReturn(staff);
        when(userRoleRepository.findByUser_UserId(any())).thenReturn(List.of());

        StaffResponse res = service.updateStaff(1L, validRequest());

        assertThat(res.getPosition()).isEqualTo("Nhân viên bán hàng");
        verify(userRepository).save(any());
        verify(staffRepository).save(any());
    }

    // ── deleteStaff ───────────────────────────────────────────────────────────

    @Test
    void deleteStaff_success() {
        Staff staff = mockStaff();
        when(staffRepository.findByUser_UserId(1L)).thenReturn(Optional.of(staff));
        when(userRoleRepository.findByUser_UserId(1L)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.deleteStaff(1L));

    verify(staffRepository).save(any()); // ✅
    verify(staffRepository, never()).delete(any());
    }

    @Test
    void deleteStaff_fail_notFound() {
        when(staffRepository.findByUser_UserId(99L)).thenReturn(Optional.empty());
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteStaff(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Staff not found");
    }
}