package com.vin.VinSystem.Auth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> findByPhone(@RequestParam String phone) {
        try {
            UserResponse user = staffCustomerService.findByPhone(phone);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage()))
                return ResponseEntity.status(404).body("Số điện thoại chưa có tài khoản");
            System.err.println("[StaffCustomerController] findByPhone error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Tạo tài khoản CUSTOMER nhanh.
     * Trả về cả username + tempPassword để staff đọc cho khách.
     */
    @PostMapping("/quick-create")
    public ResponseEntity<?> quickCreate(@RequestBody QuickCreateCustomerRequest req) {
        try {
            return ResponseEntity.ok(staffCustomerService.quickCreate(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}