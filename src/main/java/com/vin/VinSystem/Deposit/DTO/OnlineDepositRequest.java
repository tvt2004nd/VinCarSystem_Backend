package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class OnlineDepositRequest {

    @NotNull(message = "carId không được để trống")
    private Long carId;

    @NotNull(message = "branchId không được để trống")
    private Long branchId;

    @NotNull(message = "amount không được để trống")
    @DecimalMin(value = "1000", message = "Số tiền đặt cọc tối thiểu 1.000 VND")
    private BigDecimal amount;

    // ── getters & setters ──────────────────────────────────────

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}