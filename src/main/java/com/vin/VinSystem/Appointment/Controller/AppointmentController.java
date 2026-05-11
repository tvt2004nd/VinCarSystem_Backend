package com.vin.VinSystem.Appointment.Controller;

import com.vin.VinSystem.Appointment.DTO.*;
import com.vin.VinSystem.Appointment.Service.AppointmentService;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Common.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public AppointmentController(
            AppointmentService appointmentService,
            UserRepository userRepository
    ) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ApiResponse<List<AppointmentResponseDTO>> getByBranch(@PathVariable Long branchId) {
        return ApiResponse.success(appointmentService.getAppointmentsByBranch(branchId));
    }

    @PostMapping("/create")
    public ApiResponse<AppointmentResponseDTO> createAppointment(
            Authentication authentication,
            @RequestBody CreateAppointmentDTO dto
    ) {
        String username = authentication.getName();
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        AppointmentResponseDTO response = appointmentService.createAppointment(customer, dto);
        return ApiResponse.success(response, "Đặt lịch thành công");
    }

    @GetMapping("/my")
    public ApiResponse<List<AppointmentResponseDTO>> myAppointments(Authentication authentication) {
        String username = authentication.getName();
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ApiResponse.success(appointmentService.getCustomerAppointments(customer.getUserId()));
    }

    @GetMapping("/staff")
    public ApiResponse<List<AppointmentResponseDTO>> staffAppointments(Authentication authentication) {
        String username = authentication.getName();
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ApiResponse.success(appointmentService.getStaffAppointments(staff.getUserId()));
    }

    @PutMapping("/update")
    public ApiResponse<AppointmentResponseDTO> updateAppointment(
            Authentication authentication,
            @RequestBody UpdateAppointmentDTO dto
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        AppointmentResponseDTO response = appointmentService.updateAppointment(dto, user.getUserId());
        return ApiResponse.success(response, "Cập nhật lịch thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancelAppointment(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        appointmentService.cancelAppointmentByUser(id, user.getUserId());
        return ApiResponse.success(null, "Hủy lịch thành công");
    }

    @PutMapping("/{id}/status")
    public ApiResponse<AppointmentResponseDTO> staffUpdateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        AppointmentResponseDTO response = appointmentService.staffUpdateStatus(id, request.getStatus());
        return ApiResponse.success(response, "Cập nhật trạng thái thành công");
    }

    @GetMapping("/{id}")
    public ApiResponse<AppointmentResponseDTO> getAppointmentDetail(@PathVariable Long id) {
        return ApiResponse.success(appointmentService.getAppointmentById(id));
    }
}