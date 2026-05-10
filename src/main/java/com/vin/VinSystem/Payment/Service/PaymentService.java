package com.vin.VinSystem.Payment.Service;

import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;
import com.vin.VinSystem.Deposit.Service.DepositService;
import com.vin.VinSystem.Notification.Service.ContractPdfService;
import com.vin.VinSystem.Notification.Service.NotificationService;
import com.vin.VinSystem.Payment.DTO.PaymentDTO;
import com.vin.VinSystem.Payment.DTO.PaymentRequest;
import com.vin.VinSystem.Payment.Entity.Payment;
import com.vin.VinSystem.Payment.Repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED    = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_REFUNDED  = "REFUNDED";

    @Autowired private PaymentRepository   paymentRepository;
    @Autowired private DepositRepository   depositRepository;
    @Autowired private DepositService      depositService;
    @Autowired private NotificationService notificationService;
    @Autowired private ContractPdfService  contractPdfService;

    // ── QUERY ────────────────────────────────────────────────────────────────

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        return toDTO(paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id)));
    }

    public PaymentDTO getPaymentByTxnRef(String txnRef) {
        return toDTO(paymentRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found for txnRef: " + txnRef)));
    }

    public List<PaymentDTO> getPaymentsByDeposit(Long depositId) {
        return paymentRepository.findByDeposit_DepositId(depositId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByDeposit_Customer_UserId(customerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Transactional
    public PaymentDTO createPayment(PaymentRequest request) {
        Deposit deposit = depositRepository.findById(request.getDepositId())
                .orElseThrow(() -> new RuntimeException("Deposit not found: " + request.getDepositId()));

        if (STATUS_CANCELLED.equals(deposit.getStatus()))
            throw new RuntimeException("Đơn đặt cọc đã bị hủy.");

        Payment payment = new Payment();
        payment.setDeposit(deposit);
        payment.setProvider(request.getProvider());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(STATUS_PENDING);

        String paymentType = request.getPaymentType();
        payment.setPaymentType(paymentType != null ? paymentType : "DEPOSIT");

        paymentRepository.save(payment);
        log.info("[Payment] created paymentId={} depositId={} paymentType={}",
                 payment.getPaymentId(), request.getDepositId(), payment.getPaymentType());
        return toDTO(payment);
    }

    @Transactional
    public void updateTxnRef(Long paymentId, String txnRef) {
        paymentRepository.findById(paymentId).ifPresent(p -> {
            p.setTxnRef(txnRef);
            paymentRepository.save(p);
            log.info("[Payment] updateTxnRef paymentId={} txnRef={}", paymentId, txnRef);
        });
    }

    // ── CALLBACK ─────────────────────────────────────────────────────────────

    @Transactional
    public void updateStatusByTxnRef(String txnRef, String newStatus) {
        log.info("[Payment] updateStatusByTxnRef txnRef={} newStatus={}", txnRef, newStatus);

        paymentRepository.findByTxnRef(txnRef).ifPresentOrElse(payment -> {
            String current = payment.getPaymentStatus();

            if (STATUS_COMPLETED.equals(current) || STATUS_FAILED.equals(current)
                    || STATUS_CANCELLED.equals(current)) {
                log.info("[Payment] skip terminal paymentId={}", payment.getPaymentId());
                return;
            }

            payment.setPaymentStatus(newStatus);
            paymentRepository.save(payment);
            log.info("[Payment] updated paymentId={} {} → {}", payment.getPaymentId(), current, newStatus);

            Deposit deposit    = payment.getDeposit();
            String paymentType = payment.getPaymentType();

            if (STATUS_COMPLETED.equals(newStatus)) {
                if (!"FULL_PAYMENT".equalsIgnoreCase(paymentType)) {
                    // DEPOSIT → approve đơn cọc
                    depositService.markPaid(deposit.getDepositId());
                } else {
                    // FULL_PAYMENT → gửi biên lai PDF
                    log.info("[Payment] FULL_PAYMENT completed depositId={}", deposit.getDepositId());
                    sendFullPaymentReceipt(payment, deposit);
                }
            } else if (STATUS_FAILED.equals(newStatus) || STATUS_CANCELLED.equals(newStatus)) {
                depositService.markFailed(deposit.getDepositId());
            }

        }, () -> log.warn("[Payment] txnRef không tồn tại: {}", txnRef));
    }

    private void sendFullPaymentReceipt(Payment payment, Deposit deposit) {
        try {
            String carName = deposit.getCar() != null ? deposit.getCar().getCarName() : "—";

            byte[] pdf = contractPdfService.generatePaymentConfirmation(
                    deposit.getDepositId(),
                    payment.getPaymentId(),
                    deposit.getCustomer() != null ? deposit.getCustomer().getName() : "—",
                    carName,
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getTxnRef(),
                    payment.getPaymentDate()
            );

            if (deposit.getCustomer() != null) {
                notificationService.notifyFullPaymentCompleted(
                        deposit.getCustomer(),
                        deposit.getDepositId(),
                        payment.getPaymentId(),
                        carName,
                        payment.getAmount() != null ? payment.getAmount().doubleValue() : 0,
                        pdf
                );
            }
        } catch (Exception e) {
            log.error("[Payment] sendFullPaymentReceipt failed paymentId={} err={}",
                      payment.getPaymentId(), e.getMessage());
        }
    }

    // ── MANUAL ───────────────────────────────────────────────────────────────

    @Transactional
    public PaymentDTO updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
        if (STATUS_FAILED.equals(status) || STATUS_CANCELLED.equals(status))
            depositService.markFailed(payment.getDeposit().getDepositId());
        return toDTO(payment);
    }

    @Transactional public PaymentDTO refundPayment(Long id) { return updatePaymentStatus(id, STATUS_REFUNDED);  }
    @Transactional public PaymentDTO cancelPayment(Long id)  { return updatePaymentStatus(id, STATUS_CANCELLED); }

    // ── TO DTO ───────────────────────────────────────────────────────────────

    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setProvider(payment.getProvider());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentType(payment.getPaymentType());
        dto.setTxnRef(payment.getTxnRef());

        Deposit deposit = payment.getDeposit();
        if (deposit != null) {
            dto.setDepositId(deposit.getDepositId());
            dto.setDepositAmount(deposit.getDepositAmount());
            dto.setDepositStatus(deposit.getStatus());
            if (deposit.getCustomer() != null) {
                dto.setCustomerName(deposit.getCustomer().getName());
                dto.setCustomerEmail(deposit.getCustomer().getEmail());
            }
            if (deposit.getCar()    != null) dto.setCarName(deposit.getCar().getCarName());
            if (deposit.getBranch() != null) dto.setBranchName(deposit.getBranch().getBranchName());
        }
        return dto;
    }
}