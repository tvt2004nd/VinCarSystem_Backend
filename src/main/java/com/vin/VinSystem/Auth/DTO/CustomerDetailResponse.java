package com.vin.VinSystem.Auth.DTO;

import java.util.List;

public class CustomerDetailResponse {

    // ── Thông tin cơ bản ─────────────────────────
    private Long   userId;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private String role;

    // ── Thống kê nhanh ───────────────────────────
    private long totalDeposits;
    private long totalPayments;
    private long totalAppointments;

    // ── Lịch sử ─────────────────────────────────
    private List<DepositSummary>     deposits;
    private List<PaymentSummary>     payments;
    private List<AppointmentSummary> appointments;

    // ══════════════════════════════════════════════
    //  Inner DTOs
    // ══════════════════════════════════════════════

    public static class DepositSummary {
        private Long   depositId;
        private Double depositAmount;
        private String depositDate;
        private String status;
        private String depositType;   // ONLINE / OFFLINE
        private String carName;       // tên xe đặt cọc
        private String branchName;

        public DepositSummary(Long depositId, Double depositAmount, String depositDate,
                              String status, String depositType,
                              String carName, String branchName) {
            this.depositId    = depositId;
            this.depositAmount = depositAmount;
            this.depositDate  = depositDate;
            this.status       = status;
            this.depositType  = depositType;
            this.carName      = carName;
            this.branchName   = branchName;
        }

        public Long   getDepositId()    { return depositId;    }
        public Double getDepositAmount(){ return depositAmount; }
        public String getDepositDate()  { return depositDate;  }
        public String getStatus()       { return status;       }
        public String getDepositType()  { return depositType;  }
        public String getCarName()      { return carName;      }
        public String getBranchName()   { return branchName;   }
    }

    public static class PaymentSummary {
        private Long   paymentId;
        private Double amount;
        private String paymentStatus;
        private String paymentMethod;
        private String paymentDate;

        public PaymentSummary(Long paymentId, Double amount,
                              String paymentStatus, String paymentMethod,
                              String paymentDate) {
            this.paymentId     = paymentId;
            this.amount        = amount;
            this.paymentStatus = paymentStatus;
            this.paymentMethod = paymentMethod;
            this.paymentDate   = paymentDate;
        }

        public Long   getPaymentId()     { return paymentId;     }
        public Double getAmount()        { return amount;        }
        public String getPaymentStatus() { return paymentStatus; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getPaymentDate()   { return paymentDate;   }
    }

    public static class AppointmentSummary {
        private Long   appointmentId;
        private String purpose;        // mục đích hẹn (thay serviceName)
        private String note;
        private String staffName;      // staff.getUser().getUsername()
        private String branchName;
        private String carName;        // xe liên quan (nếu có)
        private String status;
        private String appointmentDate;

        public AppointmentSummary(Long appointmentId, String purpose, String note,
                                  String staffName, String branchName,
                                  String carName, String status, String appointmentDate) {
            this.appointmentId   = appointmentId;
            this.purpose         = purpose;
            this.note            = note;
            this.staffName       = staffName;
            this.branchName      = branchName;
            this.carName         = carName;
            this.status          = status;
            this.appointmentDate = appointmentDate;
        }

        public Long   getAppointmentId()   { return appointmentId;   }
        public String getPurpose()         { return purpose;         }
        public String getNote()            { return note;            }
        public String getStaffName()       { return staffName;       }
        public String getBranchName()      { return branchName;      }
        public String getCarName()         { return carName;         }
        public String getStatus()          { return status;          }
        public String getAppointmentDate() { return appointmentDate; }
    }

    // ══════════════════════════════════════════════
    //  Constructor
    // ══════════════════════════════════════════════

    public CustomerDetailResponse(
            Long userId, String username, String name, String email,
            String phoneNumber, String address, String avatar, String role,
            long totalDeposits, long totalPayments, long totalAppointments,
            List<DepositSummary> deposits,
            List<PaymentSummary> payments,
            List<AppointmentSummary> appointments) {

        this.userId            = userId;
        this.username          = username;
        this.name              = name;
        this.email             = email;
        this.phoneNumber       = phoneNumber;
        this.address           = address;
        this.avatar            = avatar;
        this.role              = role;
        this.totalDeposits     = totalDeposits;
        this.totalPayments     = totalPayments;
        this.totalAppointments = totalAppointments;
        this.deposits          = deposits;
        this.payments          = payments;
        this.appointments      = appointments;
    }

    // ══════════════════════════════════════════════
    //  Getters
    // ══════════════════════════════════════════════

    public Long   getUserId()            { return userId;            }
    public String getUsername()          { return username;          }
    public String getName()              { return name;              }
    public String getEmail()             { return email;             }
    public String getPhoneNumber()       { return phoneNumber;       }
    public String getAddress()           { return address;           }
    public String getAvatar()            { return avatar;            }
    public String getRole()              { return role;              }
    public long   getTotalDeposits()     { return totalDeposits;     }
    public long   getTotalPayments()     { return totalPayments;     }
    public long   getTotalAppointments() { return totalAppointments; }
    public List<DepositSummary>     getDeposits()     { return deposits;     }
    public List<PaymentSummary>     getPayments()     { return payments;     }
    public List<AppointmentSummary> getAppointments() { return appointments; }
}