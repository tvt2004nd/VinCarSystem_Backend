package com.vin.VinSystem.Auth.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Appointment.Entity.Appointment;
import com.vin.VinSystem.Appointment.Repository.AppointmentRepository;
import com.vin.VinSystem.Auth.DTO.CustomerDetailResponse;
import com.vin.VinSystem.Auth.DTO.CustomerResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;
import com.vin.VinSystem.Payment.Entity.Payment;
import com.vin.VinSystem.Payment.Repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class AdminCustomerServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private DepositRepository depositRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AdminCustomerService adminCustomerService;

    private User user;
    private UserRole userRole;
    private Deposit deposit;
    private Payment payment;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        // === Tạo real object ===
        user = new User();
        user.setUserId(100L);                    // User có setter
        user.setUsername("customer001");
        user.setName("Nguyễn Văn A");
        user.setEmail("a@gmail.com");
        user.setPhoneNumber("0987654321");
        user.setAddress("Hà Nội");
        user.setAvatar("avatar.jpg");

        // Role
        Role role = new Role();
        role.setRoleName("CUSTOMER");
        userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        // Deposit
        deposit = new Deposit();
        ReflectionTestUtils.setField(deposit, "depositId", 1L);           // ← fix lỗi này
        ReflectionTestUtils.setField(deposit, "depositAmount", BigDecimal.valueOf(5000000));
        ReflectionTestUtils.setField(deposit, "depositDate", LocalDateTime.now());
        ReflectionTestUtils.setField(deposit, "status", "COMPLETED");
        ReflectionTestUtils.setField(deposit, "depositType", "BOOKING");

        // Payment
        payment = new Payment();
        ReflectionTestUtils.setField(payment, "paymentId", 10L);
        ReflectionTestUtils.setField(payment, "amount", BigDecimal.valueOf(25000000));
        ReflectionTestUtils.setField(payment, "paymentStatus", "PAID");
        ReflectionTestUtils.setField(payment, "paymentMethod", "BANK_TRANSFER");
        ReflectionTestUtils.setField(payment, "paymentDate", LocalDateTime.now());

        // Appointment
        appointment = new Appointment();
        ReflectionTestUtils.setField(appointment, "appointmentId", 5L);
        ReflectionTestUtils.setField(appointment, "purpose", "Xem xe");
        ReflectionTestUtils.setField(appointment, "note", "Khách muốn xem mẫu XYZ");
        ReflectionTestUtils.setField(appointment, "status", "CONFIRMED");
        ReflectionTestUtils.setField(appointment, "appointmentDate", LocalDateTime.now());
        // Nếu entity có helper method thì set thêm
        // ReflectionTestUtils.setField(appointment, "staffName", "staff01");
        // ReflectionTestUtils.setField(appointment, "branchName", "Chi nhánh Hà Nội");
        // ReflectionTestUtils.setField(appointment, "carName", "Toyota Camry");
    }

    // ==================== TEST searchCustomers ====================
    @Test
    void searchCustomers_ShouldReturnPagedCustomers_WhenKeywordMatches() {
        String keyword = "Nguyễn";
        int page = 0, size = 10;

        PageRequest pageable = PageRequest.of(page, size, Sort.by("userId").descending());
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findDistinctByUserRoles_Role_RoleNameAndNameContainingIgnoreCaseOrUserRoles_Role_RoleNameAndPhoneNumberContaining(
                "CUSTOMER", keyword, "CUSTOMER", keyword, pageable))
                .thenReturn(userPage);

        Page<CustomerResponse> result = adminCustomerService.searchCustomers(keyword, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Nguyễn Văn A", result.getContent().get(0).getName());
    }

    // ==================== TEST getCustomerDetail ====================
    @Test
    void getCustomerDetail_ShouldReturnFullDetail_WhenUserExists() {
        Long userId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUser(user)).thenReturn(List.of(userRole));
        when(depositRepository.findAllByCustomerFull(userId)).thenReturn(List.of(deposit));
        when(paymentRepository.findByDeposit_Customer_UserId(userId)).thenReturn(List.of(payment));
        when(appointmentRepository.findByCustomerUserId(userId)).thenReturn(List.of(appointment));

        CustomerDetailResponse result = adminCustomerService.getCustomerDetail(userId);

        assertNotNull(result);
        assertEquals(100L, result.getUserId());
        assertEquals("Nguyễn Văn A", result.getName());
        assertEquals("CUSTOMER", result.getRole());
        assertEquals(1, result.getTotalDeposits());
        assertEquals(1, result.getTotalPayments());
        assertEquals(1, result.getTotalAppointments());
    }

    @Test
    void getCustomerDetail_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminCustomerService.getCustomerDetail(userId));

        assertTrue(ex.getMessage().contains("Không tìm thấy khách hàng ID: 999"));
    }

    @Test
    void getCustomerDetail_ShouldHandleEmptyLists_WhenNoTransactions() {
        Long userId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUser(user)).thenReturn(List.of(userRole));
        when(depositRepository.findAllByCustomerFull(userId)).thenReturn(List.of());
        when(paymentRepository.findByDeposit_Customer_UserId(userId)).thenReturn(List.of());
        when(appointmentRepository.findByCustomerUserId(userId)).thenReturn(List.of());

        CustomerDetailResponse result = adminCustomerService.getCustomerDetail(userId);

        assertEquals(0, result.getTotalDeposits());
        assertEquals(0, result.getTotalPayments());
        assertEquals(0, result.getTotalAppointments());
    }
}