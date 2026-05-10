package com.vin.VinSystem.Appointment.Controller;

import com.vin.VinSystem.Appointment.DTO.AppointmentResponseDTO;
import com.vin.VinSystem.Appointment.DTO.AssignStaffRequest;
import com.vin.VinSystem.Appointment.DTO.UpdateStatusRequest;
import com.vin.VinSystem.Appointment.Entity.Appointment;
import com.vin.VinSystem.Appointment.Repository.AppointmentRepository;
import com.vin.VinSystem.Appointment.Service.AppointmentService;
import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;


import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminAppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final StaffRepository staffRepository;
    private final AppointmentService appointmentService;

    public AdminAppointmentController(
            AppointmentRepository appointmentRepository,
            StaffRepository staffRepository,
            AppointmentService appointmentService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.staffRepository       = staffRepository;
        this.appointmentService    = appointmentService;
    }

    /*
    ===============================
    GET ALL APPOINTMENTS
    ===============================
    */

    @GetMapping("/appointments")
    public List<AppointmentResponseDTO> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(appointmentService::toDTO)  // dùng lại toDTO từ Service
                .collect(Collectors.toList());
    }

    /*
    ===============================
    STAFF LIST
    ===============================
    */

    @GetMapping("/staffs")
    public List<StaffDTO> getStaffs() {

        return staffRepository.findAll()
                .stream()
                .map(staff -> {

                    StaffDTO dto = new StaffDTO();

                    dto.setStaffId(staff.getUserId());

                    dto.setUsername(
                        staff.getUser() != null
                            ? staff.getUser().getUsername()
                            : "Unknown"
                    );

                    return dto;

                })
                .collect(Collectors.toList());
    }

    /*
    ===============================
    ASSIGN STAFF
    ===============================
    */

    @PutMapping("/appointments/{id}/assign")
    public AppointmentResponseDTO assignStaff(
            @PathVariable Long id,
            @RequestBody AssignStaffRequest request
    ) {
        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Staff staff = staffRepository
                .findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        appointment.setStaff(staff);
        appointment.setStatus("CONFIRMED");

        return appointmentService.toDTO(
            appointmentRepository.save(appointment)
        );
    }

    /*
    ===============================
    UPDATE STATUS
    ===============================
    */

    @PutMapping("/appointments/{id}/status")
    public AppointmentResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(request.getStatus());

        return appointmentService.toDTO(
            appointmentRepository.save(appointment)
        );
    }

    /*
    ===============================
    CANCEL APPOINTMENT
    ===============================
    */

    @DeleteMapping("/appointments/{id}")
    public String cancelAppointment(@PathVariable Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);

        return "Cancelled";
    }
    // Thêm vào AdminAppointmentController — lấy lịch hẹn theo staff + ngày
@GetMapping("/appointments/staff/{staffId}")
public List<AppointmentResponseDTO> getAppointmentsByStaff(
        @PathVariable Long staffId
) {
    return appointmentRepository
            .findByStaffUserId(staffId)
            .stream()
            .map(appointmentService::toDTO)
            .collect(Collectors.toList());
}
/*
===============================
STAFF: UPDATE STATUS
(Staff chỉ được CONFIRMED → COMPLETED)
===============================
*/
@PutMapping("/appointments/{id}/staff-status")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public AppointmentResponseDTO staffUpdateStatus(
        @PathVariable Long id,
        @RequestBody UpdateStatusRequest request,
        Authentication authentication
) {
    // Chỉ cho phép CONFIRMED hoặc COMPLETED
    String status = request.getStatus();
    if (!status.equals("CONFIRMED") && !status.equals("COMPLETED")) {
        throw new RuntimeException("Staff chỉ được xác nhận hoặc hoàn thành lịch");
    }

    Appointment appointment = appointmentRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

    appointment.setStatus(status);

    return appointmentService.toDTO(
        appointmentRepository.save(appointment)
    );
}
    /*
    ===============================
    DTO FOR STAFF
    ===============================
    */

    public static class StaffDTO {

        private Long staffId;
        private String username;

        public Long getStaffId()              { return staffId; }
        public void setStaffId(Long staffId)  { this.staffId = staffId; }

        public String getUsername()                 { return username; }
        public void setUsername(String username)    { this.username = username; }
    }
}