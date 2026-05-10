package com.vin.VinSystem.Dashboard.Response;

import java.math.BigDecimal;
import java.util.List;

import com.vin.VinSystem.Dashboard.DTO.RevenueDTO;
import com.vin.VinSystem.Dashboard.DTO.StatusDTO;
import com.vin.VinSystem.Dashboard.DTO.RecentTransactionDTO;

public class DashboardResponse {

    private Long totalCars;
    private Long totalDeposits;
    private Long totalAppointments;
    private BigDecimal totalRevenue;

    private List<RevenueDTO> revenue;
    private List<StatusDTO> depositStatus;
    private List<StatusDTO> appointmentStatus;

    private List<RecentTransactionDTO> recentTransactions;

    // getters & setters
    public Long getTotalCars() { return totalCars; }
    public void setTotalCars(Long totalCars) { this.totalCars = totalCars; }

    public Long getTotalDeposits() { return totalDeposits; }
    public void setTotalDeposits(Long totalDeposits) { this.totalDeposits = totalDeposits; }

    public Long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(Long totalAppointments) { this.totalAppointments = totalAppointments; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public List<RevenueDTO> getRevenue() { return revenue; }
    public void setRevenue(List<RevenueDTO> revenue) { this.revenue = revenue; }

    public List<StatusDTO> getDepositStatus() { return depositStatus; }
    public void setDepositStatus(List<StatusDTO> depositStatus) { this.depositStatus = depositStatus; }

    public List<StatusDTO> getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(List<StatusDTO> appointmentStatus) { this.appointmentStatus = appointmentStatus; }

    public List<RecentTransactionDTO> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<RecentTransactionDTO> recentTransactions) { 
        this.recentTransactions = recentTransactions; 
    }
}
