package com.vin.VinSystem.Branch.Controller;

import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Service.BranchService;
import com.vin.VinSystem.Common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/branches")

public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public ApiResponse<List<Branch>> getAll() {
        return ApiResponse.success(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ApiResponse<Branch> getById(@PathVariable Long id) {
        Branch branch = branchService.getBranchById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        return ApiResponse.success(branch);
    }

    @PostMapping
    public ApiResponse<Branch> create(@RequestBody Map<String, Object> payload) {
        Branch branch = mapToBranch(payload);
        return ApiResponse.success(branchService.saveBranch(branch), "Tạo chi nhánh thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<Branch> update(@PathVariable Long id,
                                         @RequestBody Map<String, Object> payload) {
        Branch existing = branchService.getBranchById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Branch branch = mapToBranch(payload);
        branch.setBranchId(id);

        // Giữ lat/lng cũ nếu frontend không gửi lat/lng mới
        if (branch.getLatitude() == null)  branch.setLatitude(existing.getLatitude());
        if (branch.getLongitude() == null) branch.setLongitude(existing.getLongitude());

        return ApiResponse.success(branchService.saveBranch(branch), "Cập nhật chi nhánh thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ApiResponse.success(null, "Xóa chi nhánh thành công");
    }

    private Branch mapToBranch(Map<String, Object> payload) {
        Branch branch = new Branch();
        branch.setBranchName((String) payload.get("branchName"));
        branch.setLocation((String) payload.get("location"));
        branch.setContactInfo((String) payload.get("contactInfo"));

        // ✅ Nhận lat/lng trực tiếp từ frontend
        if (payload.get("latitude") != null) {
            branch.setLatitude(Double.parseDouble(payload.get("latitude").toString()));
        }
        if (payload.get("longitude") != null) {
            branch.setLongitude(Double.parseDouble(payload.get("longitude").toString()));
        }

        return branch;
    }
}