package com.vin.VinSystem.Auth.Controller;

import com.vin.VinSystem.Auth.DTO.RoleDTO;
import com.vin.VinSystem.Auth.Service.RoleService;
import com.vin.VinSystem.Common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cung cấp API quản lý Role
 * Base URL: /api/roles
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /** GET /api/roles - Lấy tất cả roles */
    @GetMapping
    public ApiResponse<List<RoleDTO>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }
}