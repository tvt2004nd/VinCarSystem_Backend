package com.vin.VinSystem.Auth.Controller;

import com.vin.VinSystem.Auth.DTO.StaffRequest;
import com.vin.VinSystem.Auth.DTO.StaffResponse;
import com.vin.VinSystem.Auth.Service.StaffService;
import com.vin.VinSystem.Common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiResponse<List<StaffResponse>> getAllStaff() {
        return ApiResponse.success(staffService.getAllStaff());
    }

    /** GET /api/staff/{id} - Lấy chi tiết 1 nhân viên */
    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getStaffById(@PathVariable Long id) {
        return ApiResponse.success(staffService.getStaffById(id));
    }
    @GetMapping("/active")
public ApiResponse<List<StaffResponse>> getActiveStaff() {
    return ApiResponse.success(staffService.getActiveStaff());
}

    /** GET /api/staff/branch/{branchId} - Lấy nhân viên theo chi nhánh */
    @GetMapping("/branch/{branchId}")
    public ApiResponse<List<StaffResponse>> getStaffByBranch(@PathVariable Long branchId) {
        return ApiResponse.success(staffService.getStaffByBranch(branchId));
    }

    /** POST /api/staff - Tạo nhân viên mới + cấp tài khoản */
    @PostMapping
    public ApiResponse<StaffResponse> createStaff(@RequestBody StaffRequest request) {
        return ApiResponse.success(staffService.createStaff(request), "Tạo nhân viên thành công");
    }
@GetMapping("/branch/{branchId}/active")
public ApiResponse<List<StaffResponse>> getActiveStaffByBranch(@PathVariable Long branchId) {
    return ApiResponse.success(staffService.getActiveStaffByBranch(branchId));
}
    /** PUT /api/staff/{id} - Cập nhật thông tin nhân viên */
    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> updateStaff(@PathVariable Long id,
                                                      @RequestBody StaffRequest request) {
        return ApiResponse.success(staffService.updateStaff(id, request), "Cập nhật nhân viên thành công");
    }

    /** DELETE /api/staff/{id} - Xóa nhân viên */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ApiResponse.success(null, "Xóa nhân viên thành công");
    }
}