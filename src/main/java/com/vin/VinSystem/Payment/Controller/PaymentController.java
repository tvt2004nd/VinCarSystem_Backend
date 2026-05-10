package com.vin.VinSystem.Payment.Controller;

import com.vin.VinSystem.Payment.DTO.PaymentDTO;
import com.vin.VinSystem.Payment.DTO.PaymentRequest;
import com.vin.VinSystem.Payment.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /** Lấy tất cả giao dịch */
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /** Xem chi tiết 1 giao dịch */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /** Lịch sử thanh toán theo deposit */
    @GetMapping("/deposit/{depositId}")
    public ResponseEntity<List<PaymentDTO>> getByDeposit(@PathVariable Long depositId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDeposit(depositId));
    }

    /** Lịch sử thanh toán theo khách hàng */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    /** Tạo giao dịch thanh toán mới */
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    /** Cập nhật trạng thái giao dịch */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
    }

    /** Hoàn tiền */
    @PatchMapping("/{id}/refund")
    public ResponseEntity<PaymentDTO> refund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    /** Hủy giao dịch */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PaymentDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.cancelPayment(id));
    }
}