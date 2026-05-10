package com.vin.VinSystem.Deposit.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;
import com.vin.VinSystem.Notification.Service.ContractPdfService;
import com.vin.VinSystem.Notification.Service.NotificationService;
import com.vin.VinSystem.Payment.Entity.Payment;
import com.vin.VinSystem.Payment.Repository.PaymentRepository;

class DepositServiceTest {

    DepositRepository   depositRepository   = mock(DepositRepository.class);
    UserRepository      userRepository      = mock(UserRepository.class);
    StaffRepository     staffRepository     = mock(StaffRepository.class);
    PaymentRepository   paymentRepository   = mock(PaymentRepository.class);
    NotificationService notificationService = mock(NotificationService.class);
    ContractPdfService  contractPdfService  = mock(ContractPdfService.class);

    DepositService service;

    @BeforeEach
    void setUp() {
        service = new DepositService(
            depositRepository, userRepository, staffRepository,
            paymentRepository, notificationService, contractPdfService
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private User mockUser(Long id, String username) {
        User u = new User();
        u.setUserId(id);
        u.setUsername(username);
        u.setName("Test User");
        u.setEmail("test@test.com");
        return u;
    }

    private Car mockCar(String status) {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setStatus(status);
        return c;
    }

    private Branch mockBranch() {
        Branch b = new Branch();
        b.setBranchId(10L);
        b.setBranchName("Chi nhánh HN");
        b.setContactInfo("1800 2656");
        return b;
    }

    private Staff mockStaff(String username) {
        User u = mockUser(7L, username);
        Staff s = new Staff();
        s.setUserId(7L);
        s.setUser(u);
        s.setBranch(mockBranch());
        return s;
    }

    private Deposit mockDeposit(String status) {
    Deposit d = new Deposit();
    // ← bỏ d.setDepositId(1L) — dùng ReflectionTestUtils thay thế
    ReflectionTestUtils.setField(d, "depositId", 1L);
    d.setStatus(status);
    d.setDepositAmount(new BigDecimal("50000000"));
    d.setCar(mockCar("ACTIVE"));
    d.setCustomer(mockUser(3L, "tien123"));
    d.setBranch(mockBranch());
    d.setDepositType("ONLINE");
    return d;
}

    private Payment mockPayment(String status) {
        Payment p = new Payment();
        p.setPaymentId(1L);
        p.setPaymentStatus(status);
        return p;
    }

    // ── createOnlineDeposit ───────────────────────────────────────────────

    @Test
    void createOnline_success() {
        User customer = mockUser(3L, "tien123");
        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));
        deposit.setDepositAmount(new BigDecimal("50000000"));

        when(userRepository.findByUsername("tien123")).thenReturn(Optional.of(customer));
        when(depositRepository.findFirstByCarAndCustomerAndStatusIn(any(), any(), any()))
            .thenReturn(Optional.empty());
        when(depositRepository.save(any())).thenReturn(mockDeposit("PENDING"));

        Deposit result = service.createOnlineDeposit(deposit, "tien123");

        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(depositRepository).save(any());
    }

    @Test
    void createOnline_fail_userNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));

        assertThatThrownBy(() -> service.createOnlineDeposit(deposit, "nobody"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void createOnline_fail_carUnavailable() {
        when(userRepository.findByUsername("tien123")).thenReturn(Optional.of(mockUser(3L, "tien123")));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("INACTIVE"));

        assertThatThrownBy(() -> service.createOnlineDeposit(deposit, "tien123"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("ngừng bán");
    }

    @Test
    void createOnline_fail_carComingSoon() {
        when(userRepository.findByUsername("tien123")).thenReturn(Optional.of(mockUser(3L, "tien123")));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("COMING_SOON"));

        assertThatThrownBy(() -> service.createOnlineDeposit(deposit, "tien123"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("chưa mở bán");
    }

    @Test
    void createOnline_fail_duplicateApproved() {
        User customer = mockUser(3L, "tien123");
        Deposit existing = mockDeposit("APPROVED");

        when(userRepository.findByUsername("tien123")).thenReturn(Optional.of(customer));
        when(depositRepository.findFirstByCarAndCustomerAndStatusIn(any(), any(), any()))
            .thenReturn(Optional.of(existing));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));

        assertThatThrownBy(() -> service.createOnlineDeposit(deposit, "tien123"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã đặt cọc thành công");
    }

    // ── createOfflineDeposit ──────────────────────────────────────────────

    @Test
    void createOffline_cash_approved() {
        Staff staff = mockStaff("tuan1");
        Deposit saved = mockDeposit("APPROVED");

        when(staffRepository.findByUser_Username("tuan1")).thenReturn(Optional.of(staff));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockUser(3L, "tien123")));
        when(depositRepository.save(any())).thenReturn(saved);
        when(paymentRepository.save(any())).thenReturn(mockPayment("COMPLETED"));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));
        deposit.setDepositAmount(new BigDecimal("50000000"));

        Deposit result = service.createOfflineDeposit(deposit, 3L, "CASH", "tuan1");

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        verify(paymentRepository).save(any());
    }

    @Test
    void createOffline_vnpay_pending() {
        Staff staff = mockStaff("tuan1");
        Deposit saved = mockDeposit("PENDING");

        when(staffRepository.findByUser_Username("tuan1")).thenReturn(Optional.of(staff));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockUser(3L, "tien123")));
        when(depositRepository.save(any())).thenReturn(saved);
        when(paymentRepository.save(any())).thenReturn(mockPayment("PENDING"));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));
        deposit.setDepositAmount(new BigDecimal("50000000"));

        Deposit result = service.createOfflineDeposit(deposit, 3L, "VNPAY", "tuan1");

        assertThat(result.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void createOffline_fail_invalidPaymentMethod() {
        Staff staff = mockStaff("tuan1");

        when(staffRepository.findByUser_Username("tuan1")).thenReturn(Optional.of(staff));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockUser(3L, "tien123")));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));

        assertThatThrownBy(() -> service.createOfflineDeposit(deposit, 3L, "BITCOIN", "tuan1"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("không hợp lệ");
    }

    @Test
    void createOffline_fail_staffNoBranch() {
        Staff staff = mockStaff("tuan1");
        staff.setBranch(null);

        when(staffRepository.findByUser_Username("tuan1")).thenReturn(Optional.of(staff));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockUser(3L, "tien123")));

        Deposit deposit = new Deposit();
        deposit.setCar(mockCar("ACTIVE"));

        assertThatThrownBy(() -> service.createOfflineDeposit(deposit, 3L, "CASH", "tuan1"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("chi nhánh");
    }

    // ── markPaid ──────────────────────────────────────────────────────────

    @Test
    void markPaid_pending_becomesApproved() {
        Deposit deposit = mockDeposit("PENDING");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(paymentRepository.findByDeposit_DepositId(1L)).thenReturn(List.of());
        when(depositRepository.save(any())).thenReturn(deposit);

        Deposit result = service.markPaid(1L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void markPaid_alreadyApproved_skip() {
        Deposit deposit = mockDeposit("APPROVED");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        Deposit result = service.markPaid(1L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        verify(depositRepository, never()).save(any());
    }

    @Test
    void markPaid_updatesPendingPayments() {
        Deposit deposit = mockDeposit("PENDING");
        Payment pending = mockPayment("PENDING");

        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(paymentRepository.findByDeposit_DepositId(1L)).thenReturn(List.of(pending));
        when(depositRepository.save(any())).thenReturn(deposit);

        service.markPaid(1L);

        assertThat(pending.getPaymentStatus()).isEqualTo("COMPLETED");
        verify(paymentRepository).save(pending);
    }

    // ── markFailed ────────────────────────────────────────────────────────

    @Test
    void markFailed_pending_becomesCancelled() {
        Deposit deposit = mockDeposit("PENDING");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(paymentRepository.findByDeposit_DepositId(1L)).thenReturn(List.of());
        when(depositRepository.save(any())).thenReturn(deposit);

        Deposit result = service.markFailed(1L);

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void markFailed_alreadyCancelled_skip() {
        Deposit deposit = mockDeposit("CANCELLED");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        service.markFailed(1L);

        verify(depositRepository, never()).save(any());
    }

    // ── markReady ─────────────────────────────────────────────────────────

    @Test
    void markReady_approved_becomesReady() {
        Deposit deposit = mockDeposit("APPROVED");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(depositRepository.save(any())).thenReturn(deposit);

        Deposit result = service.markReady(1L, "tuan1");

        assertThat(result.getStatus()).isEqualTo("READY");
        verify(notificationService).notifyCarReady(any(), eq(1L), any(), any());
    }

    @Test
    void markReady_fail_notApproved() {
        Deposit deposit = mockDeposit("PENDING");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        assertThatThrownBy(() -> service.markReady(1L, "tuan1"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("APPROVED");
    }

    // ── markCompleted ─────────────────────────────────────────────────────

    @Test
    void markCompleted_success_withRemaining() {
        Deposit deposit = mockDeposit("READY");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(depositRepository.save(any())).thenReturn(deposit);
        when(paymentRepository.save(any())).thenReturn(mockPayment("COMPLETED"));

        DepositService.CompletedResult result = service.markCompleted(
            1L, new BigDecimal("1000000000"), "CASH", "tuan1"
        );

        assertThat(result.deposit().getStatus()).isEqualTo("COMPLETED");
        assertThat(result.remainingAmount()).isEqualByComparingTo("950000000");
        verify(paymentRepository).save(any());
    }

    @Test
    void markCompleted_fail_notReady() {
        Deposit deposit = mockDeposit("APPROVED");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        assertThatThrownBy(() -> service.markCompleted(
            1L, new BigDecimal("1000000000"), "CASH", "tuan1"
        )).isInstanceOf(RuntimeException.class).hasMessageContaining("READY");
    }

    // ── cancelByCustomer ──────────────────────────────────────────────────

    @Test
    void cancelByCustomer_success() {
        Deposit deposit = mockDeposit("PENDING");
        when(userRepository.findByUsername("tien123")).thenReturn(Optional.of(mockUser(3L, "tien123")));
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(depositRepository.save(any())).thenReturn(deposit);

        Deposit result = service.cancelByCustomer(1L, "tien123");

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelByCustomer_fail_notPending() {
        Deposit deposit = mockDeposit("APPROVED");
        when(userRepository.findByUsername("tien123")).thenReturn(Optional.of(mockUser(3L, "tien123")));
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        assertThatThrownBy(() -> service.cancelByCustomer(1L, "tien123"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("PENDING");
    }

    @Test
    void cancelByCustomer_fail_wrongUser() {
        Deposit deposit = mockDeposit("PENDING");
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(mockUser(99L, "other")));
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        assertThatThrownBy(() -> service.cancelByCustomer(1L, "other"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("quyền");
    }

    // ── cancelByStaff ─────────────────────────────────────────────────────

    @Test
    void cancelByStaff_success() {
        Deposit deposit = mockDeposit("APPROVED");
        Staff staff = mockStaff("tuan1");

        when(staffRepository.findByUser_Username("tuan1")).thenReturn(Optional.of(staff));
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(depositRepository.save(any())).thenReturn(deposit);

        Deposit result = service.cancelByStaff(1L, "tuan1");

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelByStaff_fail_alreadyCancelled() {
        Deposit deposit = mockDeposit("CANCELLED");
        Staff staff = mockStaff("tuan1");

        when(staffRepository.findByUser_Username("tuan1")).thenReturn(Optional.of(staff));
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        assertThatThrownBy(() -> service.cancelByStaff(1L, "tuan1"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã bị hủy");
    }

    // ── cancelByAdmin ─────────────────────────────────────────────────────

    @Test
    void cancelByAdmin_success() {
        Deposit deposit = mockDeposit("APPROVED");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(depositRepository.save(any())).thenReturn(deposit);

        Deposit result = service.cancelByAdmin(1L);

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelByAdmin_fail_alreadyCancelled() {
        Deposit deposit = mockDeposit("CANCELLED");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        assertThatThrownBy(() -> service.cancelByAdmin(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã bị hủy");
    }
}