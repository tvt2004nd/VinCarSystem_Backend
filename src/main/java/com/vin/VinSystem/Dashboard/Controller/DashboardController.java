package com.vin.VinSystem.Dashboard.Controller;

import org.springframework.web.bind.annotation.*;
import com.vin.VinSystem.Dashboard.Service.DashboardService;
import com.vin.VinSystem.Dashboard.Response.DashboardResponse;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {
    
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

 @GetMapping
    public DashboardResponse getDashboard(
        @RequestParam(defaultValue = "month") String type,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer year,   // ← thêm
        @RequestParam(required = false) Integer month   // ← thêm
    ) {
        return service.getDashboard(type, status, search, category, year, month);
    }
}