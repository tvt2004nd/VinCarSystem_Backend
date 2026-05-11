package com.vin.VinSystem.Auth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Auth.Service.AdminCustomerService;

@RestController
@RequestMapping("/api/admin/customers")
public class AdminCustomerController {

    @Autowired
    private AdminCustomerService adminCustomerService;

    // ── Danh sách (đã có) ──────────────────────────
    @GetMapping
    public ApiResponse<Object> getCustomers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ApiResponse.success(adminCustomerService.searchCustomers(keyword, page, size));
    }

    // ── Chi tiết theo ID ───────────────────────────
    @GetMapping("/{id}")
    public ApiResponse<Object> getCustomerDetail(@PathVariable Long id) {
        return ApiResponse.success(adminCustomerService.getCustomerDetail(id));
    }
}