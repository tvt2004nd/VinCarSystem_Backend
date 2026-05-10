package com.vin.VinSystem.Appointment.Entity;

import java.time.Instant;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Car.Entity.Car;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    /*
     CUSTOMER
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User customer;

    /*
     STAFF
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff staff;

    /*
     BRANCH
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Branch branch;

    /*
     CAR (THÊM MỚI)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Car car;

    /*
     DATE
     */
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    /*
     PURPOSE
     */
    @Lob
    @Column(name = "purpose")
    private String purpose;

    /*
     NOTE
     */
    @Lob
    @Column(name = "note")
    private String note;

    /*
     STATUS
     */
    @Column(name = "status", length = 50)
    private String status;

    // ================= GETTERS =================

    public Long getAppointmentId() {
        return appointmentId;
    }

    public User getCustomer() {
        return customer;
    }

    public Staff getStaff() {
        return staff;
    }

    public Branch getBranch() {
        return branch;
    }

    public Car getCar() {
        return car;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }

    // ================= SETTERS =================

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ================= HELPER FOR FRONTEND =================

    public String getCustomerName() {
        return customer != null ? customer.getUsername() : null;
    }

    public String getStaffName() {
        return staff != null && staff.getUser() != null
                ? staff.getUser().getUsername()
                : null;
    }

    public String getBranchName() {
        return branch != null ? branch.getBranchName() : null;
    }

    public String getCarName() {
        return car != null ? car.getCarName() : null;
    }

    public Long getCarId() {
        return car != null ? car.getCarId() : null;
    }
}