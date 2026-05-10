package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;

public class DepositRequest {

    private Long carId;
    private Long branchId;
    private BigDecimal amount;

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}