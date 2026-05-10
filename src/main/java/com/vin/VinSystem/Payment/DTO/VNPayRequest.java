package com.vin.VinSystem.Payment.DTO;

import com.vin.VinSystem.Payment.DTO.PaymentRequest;
import java.math.BigDecimal;

public class VNPayRequest {

    private Long       depositId;
    private BigDecimal amount;
    private String     paymentType;
    private String     paymentMethod;
    private String     provider;

    /**
     * Phân biệt luồng thanh toán:
     * null / "customer" → customer tự đặt online
     * "staff"           → staff tạo offline, VNPay returnUrl sẽ kèm ?caller=staff
     *                     để PaymentResult redirect về /staff/deposits
     */
    private String caller;

    public Long getDepositId()               { return depositId; }
    public void setDepositId(Long depositId) { this.depositId = depositId; }

    public BigDecimal getAmount()                { return amount; }
    public void setAmount(BigDecimal amount)     { this.amount = amount; }

    public String getPaymentType()               { return paymentType; }
    public void setPaymentType(String t)         { this.paymentType = t; }

    public String getPaymentMethod()             { return paymentMethod; }
    public void setPaymentMethod(String m)       { this.paymentMethod = m; }

    public String getProvider()                  { return provider; }
    public void setProvider(String provider)     { this.provider = provider; }

    public String getCaller()                    { return caller; }
    public void setCaller(String caller)         { this.caller = caller; }

    public PaymentRequest toPaymentRequest() {
        PaymentRequest req = new PaymentRequest();
        req.setDepositId(depositId);
        req.setAmount(amount);
        req.setPaymentType(paymentType != null ? paymentType : "DEPOSIT");
        req.setPaymentMethod(paymentMethod != null ? paymentMethod : "VNPAY");
        req.setProvider(provider != null ? provider : "VNPAY");
        return req;
    }
}