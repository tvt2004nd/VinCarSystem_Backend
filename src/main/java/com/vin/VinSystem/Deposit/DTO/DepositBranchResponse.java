package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vin.VinSystem.Deposit.Entity.Deposit;

public class DepositBranchResponse {

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
    private Long   customerId;

    private String carName;
    private Long   carId;

    private String branchName;
    private String createdByStaff;

    public DepositBranchResponse(Deposit d) {
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
            this.customerId    = d.getCustomer().getUserId();
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
        if (d.getCreatedByStaff() != null) {
            this.createdByStaff = d.getCreatedByStaff().getName();
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
    public Long          getCustomerId()      { return customerId; }
    public String        getCarName()         { return carName; }
    public Long          getCarId()           { return carId; }
    public String        getBranchName()      { return branchName; }
    public String        getCreatedByStaff()  { return createdByStaff; }
}