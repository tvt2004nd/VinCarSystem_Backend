package com.vin.VinSystem.Payment.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class VNPayService {

    private static final Logger log = LoggerFactory.getLogger(VNPayService.class);

    // Số tiền cố định gửi lên sandbox để bypass giới hạn
    // Amount thật đã lưu trong DB — sandbox chỉ dùng để xác nhận responseCode
    private static final long SANDBOX_AMOUNT = 10_000L;

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.sandbox-url}")
    private String vnpUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.frontend-result-url}")
    private String frontendResultUrl;

    // true = sandbox (mặc định), false = production
    @Value("${vnpay.is-sandbox:true}")
    private boolean isSandbox;

    public String getFrontendResultUrl() { return frontendResultUrl; }

    /**
     * Resolve amount gửi lên VNPay:
     * - Sandbox: luôn dùng 10.000đ để bypass giới hạn
     * - Production: dùng số tiền thật
     *
     * ⚠️ Chỉ dùng khi BUILD URL — không dùng khi đọc callback
     * Amount thật luôn lấy từ DB theo txnRef
     */
    public long resolveVnpayAmount(long realAmount) {
        if (isSandbox) {
            log.debug("[VNPay] sandbox mode — dùng {} thay vì {}", SANDBOX_AMOUNT, realAmount);
            return SANDBOX_AMOUNT;
        }
        return realAmount;
    }

    /**
     * Tạo URL thanh toán VNPay.
     *
     * @param caller null/"customer" → returnUrl thường
     *               "staff"         → returnUrl kèm ?caller=staff
     */
    public String createPaymentUrl(long amount, String orderInfo,
                                   String txnRef, String ipAddress,
                                   String caller) {

        String effectiveReturnUrl = returnUrl;
        if ("staff".equals(caller)) {
            effectiveReturnUrl = returnUrl + (returnUrl.contains("?") ? "&" : "?") + "caller=staff";
        }

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        fmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String createDate = fmt.format(cal.getTime());
        cal.add(Calendar.MINUTE, 15);
        String expireDate = fmt.format(cal.getTime());

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version",    "2.1.0");
        params.put("vnp_Command",    "pay");
        params.put("vnp_TmnCode",    tmnCode);
        params.put("vnp_Amount",     String.valueOf(amount * 100));
        params.put("vnp_CurrCode",   "VND");
        params.put("vnp_TxnRef",     txnRef);
        params.put("vnp_OrderInfo",  orderInfo);
        params.put("vnp_OrderType",  "other");
        params.put("vnp_Locale",     "vn");
        params.put("vnp_ReturnUrl",  effectiveReturnUrl);
        params.put("vnp_IpAddr",     ipAddress);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        StringBuilder hashData    = new StringBuilder();
        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, String> e : params.entrySet()) {
            String encVal = URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII);
            hashData.append(e.getKey()).append("=").append(encVal).append("&");
            queryString.append(URLEncoder.encode(e.getKey(), StandardCharsets.US_ASCII))
                       .append("=").append(encVal).append("&");
        }
        hashData.deleteCharAt(hashData.length() - 1);
        queryString.deleteCharAt(queryString.length() - 1);

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        queryString.append("&vnp_SecureHash=").append(secureHash);

        log.info("[VNPay] createPaymentUrl txnRef={} realAmount={} vnpayAmount={} sandbox={} caller={}",
                 txnRef, amount, amount, isSandbox, caller);
        return vnpUrl + "?" + queryString;
    }

    /** Backward compatible — không có caller */
    public String createPaymentUrl(long amount, String orderInfo,
                                   String txnRef, String ipAddress) {
        return createPaymentUrl(amount, orderInfo, txnRef, ipAddress, null);
    }

    public boolean validateCallback(Map<String, String> params) {
        log.info("[VNPay] validateCallback — keys: {}", params.keySet());

        String received = params.get("vnp_SecureHash");
        if (received == null || received.isBlank()) {
            log.warn("[VNPay] validateCallback — thiếu vnp_SecureHash");
            return false;
        }

        Map<String, String> filtered = new TreeMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");
        filtered.remove("caller");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> e : filtered.entrySet()) {
            hashData.append(e.getKey()).append("=")
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII))
                    .append("&");
        }
        hashData.deleteCharAt(hashData.length() - 1);

        String calculated = hmacSHA512(hashSecret, hashData.toString());
        boolean valid = calculated.equalsIgnoreCase(received);

        if (valid) log.info("[VNPay] validateCallback — HỢP LỆ ✅");
        else       log.warn("[VNPay] validateCallback — SAI chữ ký\n calc={}\n recv={}", calculated, received);
        return valid;
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        if (ip != null && ip.contains(","))
            ip = ip.split(",")[0].trim();
        return ip;
    }

    public String generateTxnRef(Long paymentId) {
        return "VF" + paymentId + "_" + System.currentTimeMillis();
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secret =
                    new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secret);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA512 error: " + e.getMessage(), e);
        }
    }
}