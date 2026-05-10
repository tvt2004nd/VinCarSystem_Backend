package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vin.VinSystem.Deposit.Entity.Deposit;

public class DepositResponse {
    private Long depositId;
    private String status;
    private String depositType;
    private LocalDateTime depositDate;
    private BigDecimal depositAmount;

    private String carName;
    private String branchName;

    public DepositResponse(Deposit d) {
        this.depositId = d.getDepositId();
        this.status = d.getStatus();
        this.depositType = d.getDepositType();
        this.depositDate = d.getDepositDate();
        this.depositAmount = d.getDepositAmount();
        this.carName = d.getCar().getCarName();
        this.branchName = d.getBranch().getBranchName();
    }

    public Long getDepositId() {
        return depositId;
    }

    public void setDepositId(Long depositId) {
        this.depositId = depositId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public LocalDateTime getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDateTime depositDate) {
        this.depositDate = depositDate;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
}