package com.vin.VinSystem.Dashboard.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecentTransactionDTO {
    private String type; // "DEPOSIT" hoặc "APPOINTMENT"
    private String customerName;
    private String carModel;
    private LocalDateTime date;
    private BigDecimal amount;
    private String status;

    public RecentTransactionDTO(String type, String customerName, String carModel, LocalDateTime date, BigDecimal amount, String status) {
        this.type = type;
        this.customerName = customerName;
        this.carModel = carModel;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }

    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCustomerName() { return customerName; }
    public String getCarModel() { return carModel; }
    public LocalDateTime getDate() { return date; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
}