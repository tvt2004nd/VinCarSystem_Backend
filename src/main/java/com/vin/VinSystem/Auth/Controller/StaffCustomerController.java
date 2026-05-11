package com.vin.VinSystem.Auth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Auth.DTO.QuickCreateCustomerRequest;
import com.vin.VinSystem.Auth.DTO.UserResponse;
import com.vin.VinSystem.Auth.Service.StaffCustomerService;

@RestController
@RequestMapping("/api/staff/customers")
@PreAuthorize("hasRole('STAFF')")
public class StaffCustomerController {

    @Autowired
    private StaffCustomerService staffCustomerService;

    /**
     * Tìm khách theo SĐT.
     * 200 → tìm thấy
     * 404 → chưa có → FE hiện nút tạo nhanh
     */
    @GetMapping("/find")
    public ApiResponse<UserResponse> findByPhone(@RequestParam String phone) {
        return ApiResponse.success(staffCustomerService.findByPhone(phone));
    }

    /**
     * Tạo tài khoản CUSTOMER nhanh.
     * Trả về cả username + tempPassword để staff đọc cho khách.
     */
    @PostMapping("/quick-create")
    public ApiResponse<Object> quickCreate(@RequestBody QuickCreateCustomerRequest req) {
        return ApiResponse.success(staffCustomerService.quickCreate(req), "Tạo tài khoản nhanh thành công");
    }
}