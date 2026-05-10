package com.vin.VinSystem.Deposit.DTO;

import java.math.BigDecimal;
import java.util.Map;

public class DepositStatsResponse {

    private long totalDeposits;
    private BigDecimal totalAmount;
    private Map<String, Long> countByStatus;
    private Map<String, BigDecimal> amountByStatus;

    public DepositStatsResponse(long totalDeposits,
                                BigDecimal totalAmount,
                                Map<String, Long> countByStatus,
                                Map<String, BigDecimal> amountByStatus) {
        this.totalDeposits = totalDeposits;
        this.totalAmount = totalAmount;
        this.countByStatus = countByStatus;
        this.amountByStatus = amountByStatus;
    }

    public long getTotalDeposits() {
        return totalDeposits;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Map<String, Long> getCountByStatus() {
        return countByStatus;
    }

    public Map<String, BigDecimal> getAmountByStatus() {
        return amountByStatus;
    }
}