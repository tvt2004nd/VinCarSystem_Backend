package com.vin.VinSystem.Payment.Repository;

import com.vin.VinSystem.Payment.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDeposit_DepositId(Long depositId);
    List<Payment> findByPaymentStatus(String paymentStatus);
    List<Payment> findByDeposit_Customer_UserId(Long customerId);
    Optional<Payment> findByTxnRef(String txnRef); // ── Tìm theo mã GD VNPay
}