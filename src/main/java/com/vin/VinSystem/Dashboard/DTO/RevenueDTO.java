package com.vin.VinSystem.Dashboard.DTO;

import java.math.BigDecimal;

public class RevenueDTO {

    private String label;
    private BigDecimal value;

    public RevenueDTO(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public BigDecimal getValue() { return value; }
}