package com.vin.VinSystem.Auth.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vin.VinSystem.Appointment.Entity.Appointment;
import com.vin.VinSystem.Appointment.Repository.AppointmentRepository;
import com.vin.VinSystem.Auth.DTO.CustomerDetailResponse;
import com.vin.VinSystem.Auth.DTO.CustomerDetailResponse.AppointmentSummary;
import com.vin.VinSystem.Auth.DTO.CustomerDetailResponse.DepositSummary;
import com.vin.VinSystem.Auth.DTO.CustomerDetailResponse.PaymentSummary;
import com.vin.VinSystem.Auth.DTO.CustomerResponse;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;
import com.vin.VinSystem.Payment.Entity.Payment;
import com.vin.VinSystem.Payment.Repository.PaymentRepository;

@Service
public class AdminCustomerService {

    @Autowired private UserRepository        userRepository;
    @Autowired private UserRoleRepository    userRoleRepository;
    @Autowired private DepositRepository     depositRepository;
    @Autowired private PaymentRepository     paymentRepository;
    @Autowired private AppointmentRepository appointmentRepository;

    // ════════════════════════════════════════════════════════
    //  Danh sách (giữ nguyên logic cũ)
    // ════════════════════════════════════════════════════════

    public Page<CustomerResponse> searchCustomers(String keyword, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("userId").descending());

        Page<User> users = userRepository
                .findDistinctByUserRoles_Role_RoleNameAndNameContainingIgnoreCaseOrUserRoles_Role_RoleNameAndPhoneNumberContaining(
                        "CUSTOMER", keyword,
                        "CUSTOMER", keyword,
                        pageable);

        return new PageImpl<>(
                users.getContent().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()),
                pageable,
                users.getTotalElements());
    }

    private CustomerResponse convertToDTO(User user) {
        return new CustomerResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAvatar());
    }

    // ════════════════════════════════════════════════════════
    //  Chi tiết 1 customer  →  GET /api/admin/customers/{id}
    // ════════════════════════════════════════════════════════

    public CustomerDetailResponse getCustomerDetail(Long userId) {

        // ── 1. User ─────────────────────────────────────────
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + userId));

        // ── 2. Role ─────────────────────────────────────────
        String role = userRoleRepository.findByUser(user)
                .stream()
                .findFirst()
                .map(ur -> ur.getRole().getRoleName())
                .orElse("CUSTOMER");

        // ── 3. Deposits ─────────────────────────────────────
        // findAllByCustomerFull dùng LEFT JOIN FETCH car + branch + customer
        // → không bị LazyInitializationException
        List<Deposit> rawDeposits = depositRepository.findAllByCustomerFull(userId);

        List<DepositSummary> deposits = rawDeposits.stream()
                .map(d -> new DepositSummary(
                        d.getDepositId(),
                        d.getDepositAmount() != null ? d.getDepositAmount().doubleValue() : null,
                        d.getDepositDate()   != null ? d.getDepositDate().toString()      : null,
                        d.getStatus(),
                        d.getDepositType(),
                        // car và branch đã được fetch sẵn trong query
                        d.getCar()    != null ? d.getCar().getCarName()          : null,
                        d.getBranch() != null ? d.getBranch().getBranchName()    : null))
                .collect(Collectors.toList());

        // ── 4. Payments ─────────────────────────────────────
        // PaymentRepository.findByDeposit_Customer_UserId đã có sẵn
        List<Payment> rawPayments = paymentRepository.findByDeposit_Customer_UserId(userId);

        List<PaymentSummary> payments = rawPayments.stream()
                .map(p -> new PaymentSummary(
                        p.getPaymentId(),
                        p.getAmount()        != null ? p.getAmount().doubleValue()    : null,
                        p.getPaymentStatus(),
                        p.getPaymentMethod(),
                        p.getPaymentDate()   != null ? p.getPaymentDate().toString()  : null))
                .collect(Collectors.toList());

        // ── 5. Appointments ─────────────────────────────────
        // Appointment.getStaffName() và getBranchName() là helper method
        // đã được định nghĩa sẵn trong Entity → dùng trực tiếp
        List<Appointment> rawAppointments = appointmentRepository.findByCustomerUserId(userId);

        List<AppointmentSummary> appointments = rawAppointments.stream()
                .map(a -> new AppointmentSummary(
                        a.getAppointmentId(),
                        a.getPurpose(),              // mục đích hẹn (xem xe, tư vấn...)
                        a.getNote(),
                        a.getStaffName(),            // helper: staff.getUser().getUsername()
                        a.getBranchName(),           // helper: branch.getBranchName()
                        a.getCarName(),              // helper: car.getCarName()
                        a.getStatus(),
                        a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : null))
                .collect(Collectors.toList());

        // ── 6. Build & return ────────────────────────────────
        return new CustomerDetailResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAvatar(),
                role,
                deposits.size(),
                payments.size(),
                appointments.size(),
                deposits,
                payments,
                appointments);
    }
}