package com.vin.VinSystem.Payment.Service;

import java.math.BigDecimal;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;
import com.vin.VinSystem.Deposit.Service.DepositService;
import com.vin.VinSystem.Notification.Service.ContractPdfService;
import com.vin.VinSystem.Notification.Service.NotificationService;
import com.vin.VinSystem.Payment.DTO.PaymentDTO;
import com.vin.VinSystem.Payment.DTO.PaymentRequest;
import com.vin.VinSystem.Payment.Entity.Payment;
import com.vin.VinSystem.Payment.Repository.PaymentRepository;

class PaymentServiceTest {

    PaymentRepository   paymentRepository   = mock(PaymentRepository.class);
    DepositRepository   depositRepository   = mock(DepositRepository.class);
    DepositService      depositService      = mock(DepositService.class);
    NotificationService notificationService = mock(NotificationService.class);
    ContractPdfService  contractPdfService  = mock(ContractPdfService.class);
    PaymentService      service;

    @BeforeEach
    void setUp() {
        service = new PaymentService();
        ReflectionTestUtils.setField(service, "paymentRepository",   paymentRepository);
        ReflectionTestUtils.setField(service, "depositRepository",   depositRepository);
        ReflectionTestUtils.setField(service, "depositService",      depositService);
        ReflectionTestUtils.setField(service, "notificationService", notificationService);
        ReflectionTestUtils.setField(service, "contractPdfService",  contractPdfService);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Deposit mockDeposit(String status) {
        Deposit d = new Deposit();
        ReflectionTestUtils.setField(d, "depositId", 1L);
        d.setStatus(status);
        d.setDepositAmount(new BigDecimal("50000000"));
        return d;
    }

    private Payment mockPayment(String status) {
        Payment p = new Payment();
        ReflectionTestUtils.setField(p, "paymentId", 1L);
        p.setPaymentStatus(status);
        p.setPaymentType("DEPOSIT");
        p.setAmount(new BigDecimal("50000000"));
        p.setDeposit(mockDeposit("PENDING"));
        return p;
    }

    private PaymentRequest validRequest() {
        PaymentRequest r = new PaymentRequest();
        r.setDepositId(1L);
        r.setAmount(new BigDecimal("50000000"));
        r.setPaymentMethod("VNPAY");
        r.setProvider("VNPAY");
        r.setPaymentType("DEPOSIT");
        return r;
    }

    // ── getAllPayments ────────────────────────────────────────────────────

    @Test
    void getAllPayments_success() {
        when(paymentRepository.findAll()).thenReturn(List.of(mockPayment("PENDING")));

        List<PaymentDTO> result = service.getAllPayments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentStatus()).isEqualTo("PENDING");
    }

    // ── getPaymentById ────────────────────────────────────────────────────

    @Test
    void getPaymentById_found() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment("COMPLETED")));

        PaymentDTO result = service.getPaymentById(1L);

        assertThat(result.getPaymentStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void getPaymentById_notFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPaymentById(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Payment not found");
    }

    // ── getPaymentByTxnRef ────────────────────────────────────────────────

    @Test
    void getPaymentByTxnRef_found() {
        Payment p = mockPayment("PENDING");
        p.setTxnRef("TXN123");
        when(paymentRepository.findByTxnRef("TXN123")).thenReturn(Optional.of(p));

        PaymentDTO result = service.getPaymentByTxnRef("TXN123");

        assertThat(result.getTxnRef()).isEqualTo("TXN123");
    }

    @Test
    void getPaymentByTxnRef_notFound() {
        when(paymentRepository.findByTxnRef("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPaymentByTxnRef("INVALID"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("txnRef");
    }

    // ── createPayment ─────────────────────────────────────────────────────

    @Test
    void createPayment_success() {
        Deposit deposit = mockDeposit("PENDING");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(paymentRepository.save(any())).thenReturn(mockPayment("PENDING"));

        PaymentDTO result = service.createPayment(validRequest());

        assertThat(result.getPaymentStatus()).isEqualTo("PENDING");
        assertThat(result.getPaymentType()).isEqualTo("DEPOSIT");
        verify(paymentRepository).save(any());
    }

    @Test
    void createPayment_defaultType_whenNull() {
        Deposit deposit = mockDeposit("PENDING");
        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));
        when(paymentRepository.save(any())).thenReturn(mockPayment("PENDING"));

        PaymentRequest req = validRequest();
        req.setPaymentType(null);

        PaymentDTO result = service.createPayment(req);

        assertThat(result.getPaymentType()).isEqualTo("DEPOSIT");
    }

    @Test
    void createPayment_fail_depositNotFound() {
        when(depositRepository.findById(99L)).thenReturn(Optional.empty());

        PaymentRequest req = validRequest();
        req.setDepositId(99L);

        assertThatThrownBy(() -> service.createPayment(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Deposit not found");
    }

    @Test
    void createPayment_fail_depositCancelled() {
        when(depositRepository.findById(1L)).thenReturn(Optional.of(mockDeposit("CANCELLED")));

        assertThatThrownBy(() -> service.createPayment(validRequest()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã bị hủy");
    }

    // ── updateTxnRef ──────────────────────────────────────────────────────

    @Test
    void updateTxnRef_success() {
        Payment p = mockPayment("PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));

        service.updateTxnRef(1L, "TXN_NEW");

        assertThat(p.getTxnRef()).isEqualTo("TXN_NEW");
        verify(paymentRepository).save(p);
    }

    @Test
    void updateTxnRef_notFound_noOp() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> service.updateTxnRef(99L, "TXN"));
        verify(paymentRepository, never()).save(any());
    }

    // ── updateStatusByTxnRef ──────────────────────────────────────────────

    @Test
    void updateStatusByTxnRef_completed_deposit_callsMarkPaid() {
        Payment p = mockPayment("PENDING");
        p.setPaymentType("DEPOSIT");
        when(paymentRepository.findByTxnRef("TXN1")).thenReturn(Optional.of(p));
        when(depositService.markPaid(1L)).thenReturn(mockDeposit("APPROVED"));

        service.updateStatusByTxnRef("TXN1", "COMPLETED");

        assertThat(p.getPaymentStatus()).isEqualTo("COMPLETED");
        verify(depositService).markPaid(1L);
    }

    @Test
    void updateStatusByTxnRef_failed_callsMarkFailed() {
        Payment p = mockPayment("PENDING");
        when(paymentRepository.findByTxnRef("TXN1")).thenReturn(Optional.of(p));
        when(depositService.markFailed(1L)).thenReturn(mockDeposit("CANCELLED"));

        service.updateStatusByTxnRef("TXN1", "FAILED");

        assertThat(p.getPaymentStatus()).isEqualTo("FAILED");
        verify(depositService).markFailed(1L);
    }

    @Test
    void updateStatusByTxnRef_skipTerminal_alreadyCompleted() {
        Payment p = mockPayment("COMPLETED");
        when(paymentRepository.findByTxnRef("TXN1")).thenReturn(Optional.of(p));

        service.updateStatusByTxnRef("TXN1", "FAILED");

        // Không thay đổi trạng thái terminal
        assertThat(p.getPaymentStatus()).isEqualTo("COMPLETED");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void updateStatusByTxnRef_txnNotFound_silentWarn() {
        when(paymentRepository.findByTxnRef("INVALID")).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(
            () -> service.updateStatusByTxnRef("INVALID", "COMPLETED")
        );
    }

    // ── updatePaymentStatus ───────────────────────────────────────────────

    @Test
    void updatePaymentStatus_success() {
        Payment p = mockPayment("PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));
        when(depositService.markFailed(any())).thenReturn(mockDeposit("CANCELLED"));

        PaymentDTO result = service.updatePaymentStatus(1L, "CANCELLED");

        assertThat(result.getPaymentStatus()).isEqualTo("CANCELLED");
        verify(depositService).markFailed(1L);
    }

    @Test
    void updatePaymentStatus_refunded_noMarkFailed() {
        Payment p = mockPayment("COMPLETED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));

        service.updatePaymentStatus(1L, "REFUNDED");

        verify(depositService, never()).markFailed(any());
    }

    @Test
    void updatePaymentStatus_notFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePaymentStatus(99L, "CANCELLED"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Payment not found");
    }

    // ── refundPayment / cancelPayment ─────────────────────────────────────

    @Test
    void refundPayment_success() {
        Payment p = mockPayment("COMPLETED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));

        PaymentDTO result = service.refundPayment(1L);

        assertThat(result.getPaymentStatus()).isEqualTo("REFUNDED");
    }

    @Test
    void cancelPayment_success() {
        Payment p = mockPayment("PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));
        when(depositService.markFailed(any())).thenReturn(mockDeposit("CANCELLED"));

        PaymentDTO result = service.cancelPayment(1L);

        assertThat(result.getPaymentStatus()).isEqualTo("CANCELLED");
    }
}