package com.vin.VinSystem.Deposit.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Car.Entity.Car;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "deposit")
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private Long depositId;

    @Column(name = "deposit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "deposit_date", nullable = false)
    private LocalDateTime depositDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * ONLINE  — customer tự đặt qua website, thanh toán VNPay
     * OFFLINE — staff tạo tại showroom (tiền mặt hoặc VNPay tại quầy)
     */
    @Column(name = "deposit_type", length = 20, nullable = false)
    private String depositType;

    /**
     * Staff thực hiện tạo đơn (null nếu là ONLINE do customer tự tạo)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_staff_id")
    private User createdByStaff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    /** Tổng chi phí lăn bánh (giá xe + phí trước bạ + biển số + ...) */
    @Column(name = "on_road_total", precision = 18, scale = 2)
    private BigDecimal onRoadTotal;

    /** Số tiền còn lại khách cần thanh toán = onRoadTotal - depositAmount */
    @Column(name = "remaining_amount", precision = 18, scale = 2)
    private BigDecimal remainingAmount;

    /** Thời điểm hoàn tất — khi khách đến lấy xe xong */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ── getters & setters ──────────────────────────────────────────────────────

    public Long getDepositId() { return depositId; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public LocalDateTime getDepositDate() { return depositDate; }
    public void setDepositDate(LocalDateTime depositDate) { this.depositDate = depositDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getDepositType() { return depositType; }
    public void setDepositType(String depositType) { this.depositType = depositType; }

    public User getCreatedByStaff() { return createdByStaff; }
    public void setCreatedByStaff(User createdByStaff) { this.createdByStaff = createdByStaff; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public BigDecimal getOnRoadTotal() { return onRoadTotal; }
    public void setOnRoadTotal(BigDecimal onRoadTotal) { this.onRoadTotal = onRoadTotal; }

    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}