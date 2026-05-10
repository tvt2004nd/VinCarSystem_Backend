package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vin.VinSystem.Deposit.Entity.Deposit;

public class DepositAdminResponse {

    private Long          depositId;
    private String        status;
    private String        depositType;
    private LocalDateTime depositDate;
    private LocalDateTime approvedAt;
    private LocalDateTime completedAt;
    private BigDecimal    depositAmount;
    private BigDecimal    onRoadTotal;
    private BigDecimal    remainingAmount;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private String carName;
    private Long   carId;

    private String branchName;

    public DepositAdminResponse(Deposit d) {
        this.depositId       = d.getDepositId();
        this.status          = d.getStatus();
        this.depositType     = d.getDepositType();
        this.depositDate     = d.getDepositDate();
        this.approvedAt      = d.getApprovedAt();
        this.completedAt     = d.getCompletedAt();
        this.depositAmount   = d.getDepositAmount();
        this.onRoadTotal     = d.getOnRoadTotal();
        this.remainingAmount = d.getRemainingAmount();

        if (d.getCustomer() != null) {
            this.customerName  = d.getCustomer().getName();
            this.customerEmail = d.getCustomer().getEmail();
            this.customerPhone = d.getCustomer().getPhoneNumber();
        }
        if (d.getCar() != null) {
            this.carId   = d.getCar().getCarId();
            this.carName = d.getCar().getCarName();
        }
        if (d.getBranch() != null) {
            this.branchName = d.getBranch().getBranchName();
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Long          getDepositId()       { return depositId; }
    public String        getStatus()          { return status; }
    public String        getDepositType()     { return depositType; }
    public LocalDateTime getDepositDate()     { return depositDate; }
    public LocalDateTime getApprovedAt()      { return approvedAt; }
    public LocalDateTime getCompletedAt()     { return completedAt; }
    public BigDecimal    getDepositAmount()   { return depositAmount; }
    public BigDecimal    getOnRoadTotal()     { return onRoadTotal; }
    public BigDecimal    getRemainingAmount() { return remainingAmount; }
    public String        getCustomerName()    { return customerName; }
    public String        getCustomerEmail()   { return customerEmail; }
    public String        getCustomerPhone()   { return customerPhone; }
    public String        getCarName()         { return carName; }
    public Long          getCarId()           { return carId; }
    public String        getBranchName()      { return branchName; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setDepositId(Long depositId)             { this.depositId = depositId; }
    public void setStatus(String status)                 { this.status = status; }
    public void setDepositType(String depositType)       { this.depositType = depositType; }
    public void setDepositDate(LocalDateTime depositDate){ this.depositDate = depositDate; }
    public void setApprovedAt(LocalDateTime approvedAt)  { this.approvedAt = approvedAt; }
    public void setCompletedAt(LocalDateTime completedAt){ this.completedAt = completedAt; }
    public void setDepositAmount(BigDecimal depositAmount)   { this.depositAmount = depositAmount; }
    public void setOnRoadTotal(BigDecimal onRoadTotal)       { this.onRoadTotal = onRoadTotal; }
    public void setRemainingAmount(BigDecimal remainingAmount){ this.remainingAmount = remainingAmount; }
    public void setCustomerName(String customerName)     { this.customerName = customerName; }
    public void setCustomerEmail(String customerEmail)   { this.customerEmail = customerEmail; }
    public void setCustomerPhone(String customerPhone)   { this.customerPhone = customerPhone; }
    public void setCarName(String carName)               { this.carName = carName; }
    public void setCarId(Long carId)                     { this.carId = carId; }
    public void setBranchName(String branchName)         { this.branchName = branchName; }
}