package com.vin.VinSystem.Appointment.Service;

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

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            BranchRepository branchRepository,
            CarRepository carRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.branchRepository      = branchRepository;
        this.carRepository         = carRepository;
    }

    /*
    ===============================
    MAPPER: Entity → DTO
    ===============================
    */
public List<AppointmentResponseDTO> getAppointmentsByBranch(Long branchId) {
    return appointmentRepository.findByBranch_BranchId(branchId)
            .stream()
            .map(this::toDTO)
            .toList();
}
   public AppointmentResponseDTO toDTO(Appointment a) {

    // CUSTOMER
    Long customerId    = null;
    String customerName  = null;
    String customerEmail = null;
    String customerPhone = null;

    if (a.getCustomer() != null) {
        customerId    = a.getCustomer().getUserId();
        customerName  = a.getCustomer().getUsername();
        customerEmail = a.getCustomer().getEmail();
        customerPhone = a.getCustomer().getPhoneNumber();
    }

    // STAFF — Staff.userId là PK, Staff.user mới có username
    Long staffId     = null;
    String staffName = null;

    if (a.getStaff() != null) {
        staffId   = a.getStaff().getUserId();           // PK của Staff
        staffName = a.getStaff().getUser() != null
                ? a.getStaff().getUser().getUsername()
                : null;
    }

    // BRANCH
    Long branchId     = null;
    String branchName = null;

    if (a.getBranch() != null) {
        branchId   = a.getBranch().getBranchId();
        branchName = a.getBranch().getBranchName();
    }

    // CAR
    Long carId     = null;
    String carName = null;

    if (a.getCar() != null) {
        carId   = a.getCar().getCarId();
        carName = a.getCar().getCarName();
    }

    return new AppointmentResponseDTO(
            a.getAppointmentId(),
            customerId,
            customerName,
            customerEmail,
            customerPhone,
            staffId,
            staffName,
            branchId,
            branchName,
            carId,
            carName,
            a.getAppointmentDate(),
            a.getPurpose(),
            a.getNote(),
            a.getStatus()
    );
}

    /*
    ===============================
    CREATE
    ===============================
    */

    public AppointmentResponseDTO createAppointment(
            User customer,
            CreateAppointmentDTO dto
    ) {
        Branch branch = branchRepository
                .findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Car car = carRepository
                .findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setBranch(branch);
        appointment.setCar(car);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setPurpose(dto.getPurpose());

        String note = "Xe: " + car.getCarName();
        if (dto.getNote() != null && !dto.getNote().isEmpty()) {
            note += " | " + dto.getNote();
        }
        appointment.setNote(note);
        appointment.setStatus("PENDING");

        return toDTO(appointmentRepository.save(appointment));
    }

    /*
    ===============================
    CUSTOMER APPOINTMENTS
    ===============================
    */

    public List<AppointmentResponseDTO> getCustomerAppointments(
            Long customerId
    ) {
        return appointmentRepository
                .findByCustomerUserId(customerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /*
    ===============================
    STAFF APPOINTMENTS
    ===============================
    */

    public List<AppointmentResponseDTO> getStaffAppointments(
            Long staffId
    ) {
        return appointmentRepository
                .findByStaffUserId(staffId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /*
    ===============================
    UPDATE (USER)
    ===============================
    */

    public AppointmentResponseDTO updateAppointment(
            UpdateAppointmentDTO dto,
            Long userId
    ) {
        Appointment appointment = appointmentRepository
                .findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getCustomer().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa lịch này");
        }

        if (appointment.getStatus().equals("CONFIRMED") ||
            appointment.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Không thể chỉnh sửa lịch đã được xác nhận");
        }

        if (dto.getAppointmentDate() != null) {
            appointment.setAppointmentDate(dto.getAppointmentDate());
        }
        if (dto.getPurpose() != null) {
            appointment.setPurpose(dto.getPurpose());
        }
        if (dto.getNote() != null) {
            appointment.setNote(dto.getNote());
        }

        return toDTO(appointmentRepository.save(appointment));
    }

    /*
    ===============================
    CANCEL BY USER
    ===============================
    */

   public void cancelAppointmentByUser(Long id, Long userId) {

    Appointment appointment = appointmentRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

    if (!appointment.getCustomer().getUserId().equals(userId)) {
        throw new RuntimeException("Bạn không thể hủy lịch này");
    }

    // ✅ Không được hủy nếu đã CONFIRMED, COMPLETED, hoặc CANCELLED
    if (appointment.getStatus().equals("CONFIRMED")) {
        throw new RuntimeException("Lịch đã được xác nhận, vui lòng liên hệ showroom để hủy");
    }

    if (appointment.getStatus().equals("COMPLETED")) {
        throw new RuntimeException("Không thể hủy lịch đã hoàn thành");
    }

    if (appointment.getStatus().equals("CANCELLED")) {
        throw new RuntimeException("Lịch đã bị hủy trước đó");
    }

    // ✅ Không được hủy nếu còn dưới 2 tiếng
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime twoHoursBefore = appointment.getAppointmentDate().minusHours(2);

    if (now.isAfter(twoHoursBefore)) {
        throw new RuntimeException("Chỉ có thể hủy lịch trước giờ hẹn ít nhất 2 tiếng");
    }

    appointment.setStatus("CANCELLED");
    appointmentRepository.save(appointment);
}
public AppointmentResponseDTO staffUpdateStatus(Long id, String status) {

    if (!status.equals("CONFIRMED") && !status.equals("COMPLETED")) {
        throw new RuntimeException("Chỉ được cập nhật CONFIRMED hoặc COMPLETED");
    }

    Appointment appointment = appointmentRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

    // CONFIRMED chỉ được từ PENDING
    if (status.equals("CONFIRMED") && !appointment.getStatus().equals("PENDING")) {
        throw new RuntimeException("Chỉ có thể xác nhận lịch đang chờ");
    }

    // COMPLETED chỉ được từ CONFIRMED
    if (status.equals("COMPLETED") && !appointment.getStatus().equals("CONFIRMED")) {
        throw new RuntimeException("Chỉ có thể hoàn thành lịch đã xác nhận");
    }

    appointment.setStatus(status);

    return toDTO(appointmentRepository.save(appointment));
}

    /*
    ===============================
    CANCEL BY ADMIN
    ===============================
    */

    public void cancelByAdmin(Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }

    /*
    ===============================
    GET DETAIL
    ===============================
    */

    public AppointmentResponseDTO getAppointmentById(Long id) {

        return toDTO(
            appointmentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"))
        );
    }
}