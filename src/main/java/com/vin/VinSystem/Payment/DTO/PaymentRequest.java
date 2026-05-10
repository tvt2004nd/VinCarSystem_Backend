package com.vin.VinSystem.Payment.DTO;

import java.math.BigDecimal;

public class PaymentRequest {

    private Long depositId;
    private String provider;
    private BigDecimal amount;
    private String paymentMethod;  // CASH, BANK_TRANSFER, MOMO, VNPAY
    private String paymentStatus;  // PENDING, COMPLETED, FAILED, REFUNDED
    private String paymentType;    // DEPOSIT, FULL_PAYMENT ── Thêm mới

    public PaymentRequest() {}

    public Long getDepositId() { return depositId; }
    public void setDepositId(Long depositId) { this.depositId = depositId; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
}