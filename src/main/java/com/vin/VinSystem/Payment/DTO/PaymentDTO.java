package com.vin.VinSystem.Payment.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {

    private Long          paymentId;
    private Long          depositId;
    private String        customerName;
    private String        customerEmail;
    private String        carName;
    private String        branchName;
    private BigDecimal    depositAmount;
    private String        depositStatus;
    private String        provider;
    private BigDecimal    amount;
    private String        paymentMethod;
    private String        paymentStatus;
    private String        paymentType;
    private String        txnRef;        // ── Mã giao dịch VNPay
    private LocalDateTime paymentDate;

    public PaymentDTO() {}

    public Long getPaymentId()                        { return paymentId; }
    public void setPaymentId(Long paymentId)          { this.paymentId = paymentId; }

    public Long getDepositId()                        { return depositId; }
    public void setDepositId(Long depositId)          { this.depositId = depositId; }

    public String getCustomerName()                   { return customerName; }
    public void setCustomerName(String customerName)  { this.customerName = customerName; }

    public String getCustomerEmail()                  { return customerEmail; }
    public void setCustomerEmail(String customerEmail){ this.customerEmail = customerEmail; }

    public String getCarName()                        { return carName; }
    public void setCarName(String carName)            { this.carName = carName; }

    public String getBranchName()                     { return branchName; }
    public void setBranchName(String branchName)      { this.branchName = branchName; }

    public BigDecimal getDepositAmount()              { return depositAmount; }
    public void setDepositAmount(BigDecimal d)        { this.depositAmount = d; }

    public String getDepositStatus()                  { return depositStatus; }
    public void setDepositStatus(String depositStatus){ this.depositStatus = depositStatus; }

    public String getProvider()                       { return provider; }
    public void setProvider(String provider)          { this.provider = provider; }

    public BigDecimal getAmount()                     { return amount; }
    public void setAmount(BigDecimal amount)          { this.amount = amount; }

    public String getPaymentMethod()                  { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; }

    public String getPaymentStatus()                  { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus){ this.paymentStatus = paymentStatus; }

    public String getPaymentType()                    { return paymentType; }
    public void setPaymentType(String paymentType)    { this.paymentType = paymentType; }

    public String getTxnRef()                         { return txnRef; }
    public void setTxnRef(String txnRef)              { this.txnRef = txnRef; }

    public LocalDateTime getPaymentDate()             { return paymentDate; }
    public void setPaymentDate(LocalDateTime d)       { this.paymentDate = d; }
}