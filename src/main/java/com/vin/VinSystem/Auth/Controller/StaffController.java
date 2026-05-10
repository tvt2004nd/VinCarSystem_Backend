package com.vin.VinSystem.Auth.Controller;

import com.vin.VinSystem.Auth.DTO.StaffRequest;
import com.vin.VinSystem.Auth.DTO.StaffResponse;
import com.vin.VinSystem.Auth.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cung cấp API quản lý Nhân viên
 * Base URL: /api/staff
 * Chức năng: CRUD nhân viên, lọc theo chi nhánh, cấp tài khoản
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    /** GET /api/staff - Lấy tất cả nhân viên */
    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    /** GET /api/staff/{id} - Lấy chi tiết 1 nhân viên */
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }
    @GetMapping("/active")
public ResponseEntity<List<StaffResponse>> getActiveStaff() {
    return ResponseEntity.ok(staffService.getActiveStaff());
}

    /** GET /api/staff/branch/{branchId} - Lấy nhân viên theo chi nhánh */
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<StaffResponse>> getStaffByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(staffService.getStaffByBranch(branchId));
    }

    /** POST /api/staff - Tạo nhân viên mới + cấp tài khoản */
    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(@RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.createStaff(request));
    }
@GetMapping("/branch/{branchId}/active")
public ResponseEntity<List<StaffResponse>> getActiveStaffByBranch(@PathVariable Long branchId) {
    return ResponseEntity.ok(staffService.getActiveStaffByBranch(branchId));
}
    /** PUT /api/staff/{id} - Cập nhật thông tin nhân viên */
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(@PathVariable Long id,
                                                      @RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.updateStaff(id, request));
    }

    /** DELETE /api/staff/{id} - Xóa nhân viên */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}