package com.vin.VinSystem.Payment.Controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Payment.DTO.PaymentDTO;
import com.vin.VinSystem.Payment.DTO.VNPayRequest;
import com.vin.VinSystem.Payment.Service.PaymentService;
import com.vin.VinSystem.Payment.Service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    private static final Logger log = LoggerFactory.getLogger(VNPayController.class);

    @Autowired private VNPayService   vnPayService;
    @Autowired private PaymentService paymentService;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. TẠO URL THANH TOÁN
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/create-payment")
    public ApiResponse<Map<String, String>> createPayment(
            @RequestBody VNPayRequest request,
            HttpServletRequest httpRequest) {

        log.info("[VNPay] create-payment depositId={} amount={} caller={}",
                 request.getDepositId(), request.getAmount(), request.getCaller());

        // ✅ Payment lưu DB với số tiền THẬT từ request.getAmount()
        PaymentDTO payment = paymentService.createPayment(request.toPaymentRequest());
        String txnRef      = vnPayService.generateTxnRef(payment.getPaymentId());
        paymentService.updateTxnRef(payment.getPaymentId(), txnRef);

        // ✅ Chỉ resolve amount (sandbox/production) khi gửi lên VNPay
        long vnpayAmount = vnPayService.resolveVnpayAmount(request.getAmount().longValue());

        String paymentUrl = vnPayService.createPaymentUrl(
                vnpayAmount,
                "Thanh toan VinFast - " + txnRef,
                txnRef,
                vnPayService.getIpAddress(httpRequest),
                request.getCaller()
        );

        Map<String, String> resp = new HashMap<>();
        resp.put("paymentUrl", paymentUrl);
        resp.put("txnRef",     txnRef);
        resp.put("paymentId",  String.valueOf(payment.getPaymentId()));
        return ApiResponse.success(resp, "Tạo link VNPay thành công");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. PROCESS RETURN — Frontend gọi sau khi VNPay redirect về
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/process-return")
    public ApiResponse<Map<String, Object>> processReturn(
            @RequestBody Map<String, String> params) {

        String txnRef   = params.get("vnp_TxnRef");
        String respCode = params.get("vnp_ResponseCode");
        String caller   = params.get("caller");

        log.info("[VNPay] process-return txnRef={} responseCode={} caller={}", txnRef, respCode, caller);

        Map<String, Object> result = new HashMap<>();

        if (!vnPayService.validateCallback(params)) {
            log.warn("[VNPay] process-return — chữ ký không hợp lệ txnRef={}", txnRef);
            result.put("success", false);
            result.put("message", "Chữ ký không hợp lệ");
            result.put("caller",  caller);
            return ApiResponse.success(result);
        }

        if ("00".equals(respCode)) {
            paymentService.updateStatusByTxnRef(txnRef, PaymentService.STATUS_COMPLETED);
            log.info("[VNPay] process-return COMPLETED txnRef={}", txnRef);
            result.put("success", true);
            result.put("txnRef",  txnRef);
        } else {
            paymentService.updateStatusByTxnRef(txnRef, PaymentService.STATUS_FAILED);
            log.warn("[VNPay] process-return FAILED txnRef={} code={}", txnRef, respCode);
            result.put("success",      false);
            result.put("responseCode", respCode);
            result.put("message",      getErrorMessage(respCode));
        }

        result.put("caller", caller);

        // ✅ Luôn lấy amount từ DB — không dùng vnp_Amount từ callback
        // vnp_Amount sandbox = 10.000đ, không phản ánh số tiền thật
        try {
            PaymentDTO payment = paymentService.getPaymentByTxnRef(txnRef);
            result.put("amount",        payment.getAmount());   // ← số tiền thật trong DB
            result.put("depositStatus", payment.getDepositStatus());
            result.put("carName",       payment.getCarName());
            result.put("depositId",     payment.getDepositId());
        } catch (Exception e) {
            log.warn("[VNPay] process-return — không lấy được payment detail: {}", e.getMessage());
        }

        return ApiResponse.success(result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. PAYMENT STATUS
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/payment-status")
    public ApiResponse<Map<String, Object>> paymentStatus(@RequestParam String txnRef) {
        log.info("[VNPay] payment-status txnRef={}", txnRef);
        Map<String, Object> result = new HashMap<>();
        try {
            PaymentDTO p = paymentService.getPaymentByTxnRef(txnRef);
            result.put("success",       PaymentService.STATUS_COMPLETED.equals(p.getPaymentStatus()));
            result.put("status",        p.getPaymentStatus());
            result.put("txnRef",        txnRef);
            result.put("amount",        p.getAmount());
            result.put("depositId",     p.getDepositId());
            result.put("depositStatus", p.getDepositStatus());
            result.put("carName",       p.getCarName());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("[VNPay] payment-status error txnRef={} msg={}", txnRef, e.getMessage());
            throw new RuntimeException("Không tìm thấy giao dịch.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. IPN
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/ipn")
    public ApiResponse<Map<String, String>> ipn(@RequestParam Map<String, String> params) {
        log.info("[VNPay] IPN txnRef={} responseCode={}",
                 params.get("vnp_TxnRef"), params.get("vnp_ResponseCode"));
        Map<String, String> result = new HashMap<>();

        if (!vnPayService.validateCallback(params)) {
            result.put("RspCode", "97");
            result.put("Message", "Invalid Checksum");
            return ApiResponse.success(result);
        }

        String respCode = params.get("vnp_ResponseCode");
        String txnRef   = params.get("vnp_TxnRef");

        if ("00".equals(respCode)) paymentService.updateStatusByTxnRef(txnRef, PaymentService.STATUS_COMPLETED);
        else                       paymentService.updateStatusByTxnRef(txnRef, PaymentService.STATUS_FAILED);

        result.put("RspCode", "00");
        result.put("Message", "Confirm Success");
        return ApiResponse.success(result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. TẠO PAYMENT THEO ID (staff flow)
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/create-payment-by-id")
    public ApiResponse<Map<String, String>> createPaymentById(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        Long   paymentId = Long.valueOf(request.get("paymentId").toString());
        long   amount    = Long.parseLong(request.get("amount").toString());
        String caller    = request.containsKey("caller")
                           ? request.get("caller").toString() : null;

        log.info("[VNPay] create-payment-by-id paymentId={} amount={} caller={}",
                 paymentId, amount, caller);

        String txnRef = vnPayService.generateTxnRef(paymentId);
        paymentService.updateTxnRef(paymentId, txnRef);

        // ✅ Resolve amount cho sandbox trước khi gửi VNPay
        long vnpayAmount = vnPayService.resolveVnpayAmount(amount);

        String paymentUrl = vnPayService.createPaymentUrl(
                vnpayAmount,
                "Thanh toan VinFast - " + txnRef,
                txnRef,
                vnPayService.getIpAddress(httpRequest),
                caller
        );

        Map<String, String> resp = new HashMap<>();
        resp.put("paymentUrl", paymentUrl);
        resp.put("txnRef",     txnRef);
        resp.put("paymentId",  String.valueOf(paymentId));
        return ApiResponse.success(resp, "Tạo link VNPay thành công");
    }

    private String getErrorMessage(String code) {
        if (code == null) return "Giao dịch thất bại";
        return switch (code) {
            case "07" -> "Giao dịch bị nghi ngờ gian lận";
            case "09" -> "Thẻ chưa đăng ký Internet Banking";
            case "10" -> "Xác thực thông tin thẻ sai quá 3 lần";
            case "11" -> "Đã hết hạn chờ thanh toán";
            case "12" -> "Thẻ/Tài khoản bị khóa";
            case "13" -> "Sai mật khẩu OTP";
            case "24" -> "Khách hàng hủy giao dịch";
            case "51" -> "Tài khoản không đủ số dư";
            case "65" -> "Vượt hạn mức giao dịch trong ngày";
            case "75" -> "Ngân hàng đang bảo trì";
            case "79" -> "Nhập sai mật khẩu quá số lần cho phép";
            default   -> "Giao dịch thất bại (mã: " + code + ")";
        };
    }
}