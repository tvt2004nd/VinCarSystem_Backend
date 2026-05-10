package com.vin.VinSystem.Appointment.DTO;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {

    private Long appointmentId;

    // CUSTOMER
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // STAFF (Staff.userId = Staff.user.userId)
    private Long staffId;
    private String staffName;

    // BRANCH
    private Long branchId;
    private String branchName;

    // CAR
    private Long carId;
    private String carName;

    // INFO
    private LocalDateTime appointmentDate;
    private String purpose;
    private String note;
    private String status;

    // ================= CONSTRUCTOR =================

    public AppointmentResponseDTO(
            Long appointmentId,
            Long customerId,
            String customerName,
            String customerEmail,
            String customerPhone,
            Long staffId,
            String staffName,
            Long branchId,
            String branchName,
            Long carId,
            String carName,
            LocalDateTime appointmentDate,
            String purpose,
            String note,
            String status
    ) {
        this.appointmentId   = appointmentId;
        this.customerId      = customerId;
        this.customerName    = customerName;
        this.customerEmail   = customerEmail;
        this.customerPhone   = customerPhone;
        this.staffId         = staffId;
        this.staffName       = staffName;
        this.branchId        = branchId;
        this.branchName      = branchName;
        this.carId           = carId;
        this.carName         = carName;
        this.appointmentDate = appointmentDate;
        this.purpose         = purpose;
        this.note            = note;
        this.status          = status;
    }

    // ================= GETTERS =================

    public Long getAppointmentId()            { return appointmentId; }
    public Long getCustomerId()               { return customerId; }
    public String getCustomerName()           { return customerName; }
    public String getCustomerEmail()          { return customerEmail; }
    public String getCustomerPhone()          { return customerPhone; }
    public Long getStaffId()                  { return staffId; }
    public String getStaffName()              { return staffName; }
    public Long getBranchId()                 { return branchId; }
    public String getBranchName()             { return branchName; }
    public Long getCarId()                    { return carId; }
    public String getCarName()                { return carName; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getPurpose()                { return purpose; }
    public String getNote()                   { return note; }
    public String getStatus()                 { return status; }
}