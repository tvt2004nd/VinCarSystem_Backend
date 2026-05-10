package com.vin.VinSystem.Auth.Controller;

import com.vin.VinSystem.Auth.DTO.RoleDTO;
import com.vin.VinSystem.Auth.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}