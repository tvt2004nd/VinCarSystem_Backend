package com.vin.VinSystem.Appointment.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vin.VinSystem.Appointment.Entity.Appointment;
import com.vin.VinSystem.Auth.Entity.User;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // customer appointments
    List<Appointment> findByCustomerUserId(Long userId);

    // staff appointments (FIX)
    List<Appointment> findByStaffUserId(Long userId);

    // branch appointments
    List<Appointment> findByBranchBranchId(Long branchId);
    List<Appointment> findByCustomer(User customer);
List<Appointment> findByBranch_BranchId(Long branchId);
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();
}