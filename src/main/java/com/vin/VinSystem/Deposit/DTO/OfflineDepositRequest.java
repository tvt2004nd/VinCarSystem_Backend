
package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;

public class OfflineDepositRequest {

    private Long carId;
    private Long customerId;
    private BigDecimal amount;

    /**
     * CASH         — tiền mặt, STAFF xác nhận → APPROVED ngay
     * BANK_TRANSFER — chuyển khoản, STAFF xác nhận → APPROVED ngay
     * VNPAY        — gửi link VNPay cho khách → vẫn PENDING, callback mới APPROVED
     */
    private String paymentMethod;

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}