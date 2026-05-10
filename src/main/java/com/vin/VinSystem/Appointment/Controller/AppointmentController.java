package com.vin.VinSystem.Appointment.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Appointment.DTO.AppointmentResponseDTO;
import com.vin.VinSystem.Appointment.DTO.CreateAppointmentDTO;
import com.vin.VinSystem.Appointment.DTO.UpdateAppointmentDTO;
import com.vin.VinSystem.Appointment.DTO.UpdateStatusRequest;
import com.vin.VinSystem.Appointment.Service.AppointmentService;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
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
public ResponseEntity<List<AppointmentResponseDTO>> getByBranch(
        @PathVariable Long branchId) {
    return ResponseEntity.ok(appointmentService.getAppointmentsByBranch(branchId));
}
    /*
    ===============================
    CREATE APPOINTMENT
    ===============================
    */

    @PostMapping("/create")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            Authentication authentication,
            @RequestBody CreateAppointmentDTO dto
    ) {
        String username = authentication.getName();

        User customer = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppointmentResponseDTO response =
                appointmentService.createAppointment(customer, dto);

        return ResponseEntity.ok(response);
    }

    /*
    ===============================
    CUSTOMER APPOINTMENTS
    ===============================
    */

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponseDTO>> myAppointments(
            Authentication authentication
    ) {
        String username = authentication.getName();

        User customer = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AppointmentResponseDTO> list =
                appointmentService.getCustomerAppointments(customer.getUserId());

        return ResponseEntity.ok(list);
    }

    /*
    ===============================
    STAFF APPOINTMENTS
    ===============================
    */

    @GetMapping("/staff")
    public ResponseEntity<List<AppointmentResponseDTO>> staffAppointments(
            Authentication authentication
    ) {
        String username = authentication.getName();

        User staff = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AppointmentResponseDTO> list =
                appointmentService.getStaffAppointments(staff.getUserId());

        return ResponseEntity.ok(list);
    }

    /*
    ===============================
    UPDATE APPOINTMENT (USER)
    ===============================
    */

    @PutMapping("/update")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            Authentication authentication,
            @RequestBody UpdateAppointmentDTO dto
    ) {
        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppointmentResponseDTO response =
                appointmentService.updateAppointment(dto, user.getUserId());

        return ResponseEntity.ok(response);
    }

    /*
    ===============================
    CANCEL APPOINTMENT (USER)
    ===============================
    */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelAppointment(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        appointmentService.cancelAppointmentByUser(id, user.getUserId());

        return ResponseEntity.ok("Appointment cancelled");
    }
    @PutMapping("/{id}/status")
public ResponseEntity<AppointmentResponseDTO> staffUpdateStatus(
        @PathVariable Long id,
        @RequestBody UpdateStatusRequest request
) {
    return ResponseEntity.ok(
        appointmentService.staffUpdateStatus(id, request.getStatus())
    );
}
    /*
    ===============================
    APPOINTMENT DETAIL
    ===============================
    */

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentDetail(
            @PathVariable Long id
    ) {
        AppointmentResponseDTO response =
                appointmentService.getAppointmentById(id);

        return ResponseEntity.ok(response);
    }
    

}