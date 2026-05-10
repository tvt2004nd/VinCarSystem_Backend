package com.vin.VinSystem.Appointment.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vin.VinSystem.Appointment.DTO.AppointmentResponseDTO;
import com.vin.VinSystem.Appointment.DTO.CreateAppointmentDTO;
import com.vin.VinSystem.Appointment.DTO.UpdateAppointmentDTO;
import com.vin.VinSystem.Appointment.Entity.Appointment;
import com.vin.VinSystem.Appointment.Repository.AppointmentRepository;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Repository.BranchRepository;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Repository.CarRepository;

class AppointmentServiceTest {

    AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    BranchRepository      branchRepository      = mock(BranchRepository.class);
    CarRepository         carRepository         = mock(CarRepository.class);

    AppointmentService service;

    @BeforeEach
    void setUp() {
        service = new AppointmentService(appointmentRepository, branchRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private User mockUser(Long id, String username) {
        User u = new User();
        u.setUserId(id);
        u.setUsername(username);
        u.setEmail(username + "@test.com");
        u.setPhoneNumber("0912345678");
        return u;
    }

    private Car mockCar() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        return c;
    }

    private Branch mockBranch() {
        Branch b = new Branch();
        b.setBranchId(10L);
        b.setBranchName("Chi nhánh HN");
        return b;
    }

    private Appointment mockAppointment(String status, LocalDateTime date) {
        Appointment a = new Appointment();
        a.setAppointmentId(1L);
        a.setCustomer(mockUser(3L, "tien123"));
        a.setCar(mockCar());
        a.setBranch(mockBranch());
        a.setAppointmentDate(date);
        a.setPurpose("TEST_DRIVE");
        a.setNote("Xe: VinFast VF8");
        a.setStatus(status);
        return a;
    }

    // ── createAppointment ────────────────────────────────────────────────

    @Test
    void createAppointment_success() {
        User customer = mockUser(3L, "tien123");
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setBranchId(10L);
        dto.setCarId(1L);
        dto.setAppointmentDate(LocalDateTime.now().plusDays(1));
        dto.setPurpose("TEST_DRIVE");
        dto.setNote("ghi chú");

        Appointment saved = mockAppointment("PENDING", dto.getAppointmentDate());

        when(branchRepository.findById(10L)).thenReturn(Optional.of(mockBranch()));
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCar()));
        when(appointmentRepository.save(any())).thenReturn(saved);

        AppointmentResponseDTO res = service.createAppointment(customer, dto);

        assertThat(res.getStatus()).isEqualTo("PENDING");
        assertThat(res.getCarName()).isEqualTo("VinFast VF8");
        verify(appointmentRepository).save(any());
    }

    @Test
    void createAppointment_fail_branchNotFound() {
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setBranchId(99L); dto.setCarId(1L);

        assertThatThrownBy(() -> service.createAppointment(mockUser(1L,"u"), dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Branch not found");
    }

    // ── cancelAppointmentByUser ───────────────────────────────────────────

    @Test
    void cancel_success() {
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusHours(5));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatNoException().isThrownBy(() -> service.cancelAppointmentByUser(1L, 3L));
        assertThat(a.getStatus()).isEqualTo("CANCELLED");
        verify(appointmentRepository).save(a);
    }

    @Test
    void cancel_fail_wrongUser() {
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusHours(5));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.cancelAppointmentByUser(1L, 99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("không thể hủy");
    }

    @Test
    void cancel_fail_alreadyConfirmed() {
        Appointment a = mockAppointment("CONFIRMED", LocalDateTime.now().plusHours(5));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.cancelAppointmentByUser(1L, 3L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã được xác nhận");
    }

    @Test
    void cancel_fail_tooLate() {
        // Lịch hẹn chỉ còn 1 tiếng — không đủ 2 tiếng trước
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusMinutes(59));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.cancelAppointmentByUser(1L, 3L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("2 tiếng");
    }

    // ── staffUpdateStatus ─────────────────────────────────────────────────

    @Test
    void staffUpdateStatus_confirmSuccess() {
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any())).thenReturn(a);

        AppointmentResponseDTO res = service.staffUpdateStatus(1L, "CONFIRMED");
        assertThat(res.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void staffUpdateStatus_completeSuccess() {
        Appointment a = mockAppointment("CONFIRMED", LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any())).thenReturn(a);

        AppointmentResponseDTO res = service.staffUpdateStatus(1L, "COMPLETED");
        assertThat(res.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void staffUpdateStatus_fail_invalidStatus() {
        assertThatThrownBy(() -> service.staffUpdateStatus(1L, "CANCELLED"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("CONFIRMED hoặc COMPLETED");
    }

    @Test
    void staffUpdateStatus_fail_confirmNotPending() {
        // Đang CONFIRMED rồi, không thể confirm lại
        Appointment a = mockAppointment("CONFIRMED", LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.staffUpdateStatus(1L, "CONFIRMED"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đang chờ");
    }

    @Test
    void staffUpdateStatus_fail_completeNotConfirmed() {
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.staffUpdateStatus(1L, "COMPLETED"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã xác nhận");
    }

    // ── updateAppointment ─────────────────────────────────────────────────

    @Test
    void updateAppointment_success() {
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusDays(2));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any())).thenReturn(a);

        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();
        dto.setAppointmentId(1L);
        dto.setPurpose("BUY");

        AppointmentResponseDTO res = service.updateAppointment(dto, 3L);
        assertThat(res).isNotNull();
    }

    @Test
    void updateAppointment_fail_confirmed() {
        Appointment a = mockAppointment("CONFIRMED", LocalDateTime.now().plusDays(2));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        UpdateAppointmentDTO dto = new UpdateAppointmentDTO();
        dto.setAppointmentId(1L);

        assertThatThrownBy(() -> service.updateAppointment(dto, 3L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("đã được xác nhận");
    }

    // ── getAppointmentsByBranch ───────────────────────────────────────────

    @Test
    void getAppointmentsByBranch_success() {
        Appointment a = mockAppointment("PENDING", LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findByBranch_BranchId(10L)).thenReturn(List.of(a));

        List<AppointmentResponseDTO> result = service.getAppointmentsByBranch(10L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBranchName()).isEqualTo("Chi nhánh HN");
    }
}