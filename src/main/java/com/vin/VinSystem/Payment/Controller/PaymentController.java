package com.vin.VinSystem.Payment.Controller;

import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Payment.DTO.PaymentDTO;
import com.vin.VinSystem.Payment.DTO.PaymentRequest;
import com.vin.VinSystem.Payment.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiResponse<List<PaymentDTO>> getAllPayments() {
        return ApiResponse.success(paymentService.getAllPayments());
    }

    /** Xem chi tiết 1 giao dịch */
    @GetMapping("/{id}")
    public ApiResponse<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return ApiResponse.success(paymentService.getPaymentById(id));
    }

    /** Lịch sử thanh toán theo deposit */
    @GetMapping("/deposit/{depositId}")
    public ApiResponse<List<PaymentDTO>> getByDeposit(@PathVariable Long depositId) {
        return ApiResponse.success(paymentService.getPaymentsByDeposit(depositId));
    }

    /** Lịch sử thanh toán theo khách hàng */
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<PaymentDTO>> getByCustomer(@PathVariable Long customerId) {
        return ApiResponse.success(paymentService.getPaymentsByCustomer(customerId));
    }

    /** Tạo giao dịch thanh toán mới */
    @PostMapping
    public ApiResponse<PaymentDTO> createPayment(@RequestBody PaymentRequest request) {
        return ApiResponse.success(paymentService.createPayment(request), "Tạo giao dịch thành công");
    }

    /** Cập nhật trạng thái giao dịch */
    @PatchMapping("/{id}/status")
    public ApiResponse<PaymentDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ApiResponse.success(paymentService.updatePaymentStatus(id, status), "Cập nhật trạng thái thành công");
    }

    /** Hoàn tiền */
    @PatchMapping("/{id}/refund")
    public ApiResponse<PaymentDTO> refund(@PathVariable Long id) {
        return ApiResponse.success(paymentService.refundPayment(id), "Hoàn tiền thành công");
    }

    /** Hủy giao dịch */
    @PatchMapping("/{id}/cancel")
    public ApiResponse<PaymentDTO> cancel(@PathVariable Long id) {
        return ApiResponse.success(paymentService.cancelPayment(id), "Hủy giao dịch thành công");
    }
}