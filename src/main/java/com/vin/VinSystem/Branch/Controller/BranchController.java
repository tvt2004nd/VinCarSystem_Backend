package com.vin.VinSystem.Branch.Controller;

import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/branches")

public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public ResponseEntity<List<Branch>> getAll() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Branch> getById(@PathVariable Long id) {
        Branch branch = branchService.getBranchById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"));
        return ResponseEntity.ok(branch);
    }

    @PostMapping
    public ResponseEntity<Branch> create(@RequestBody Map<String, Object> payload) {
        Branch branch = mapToBranch(payload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(branchService.saveBranch(branch));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Branch> update(@PathVariable Long id,
                                         @RequestBody Map<String, Object> payload) {
        Branch existing = branchService.getBranchById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"));

        Branch branch = mapToBranch(payload);
        branch.setBranchId(id);

        // Giữ lat/lng cũ nếu frontend không gửi lat/lng mới
        if (branch.getLatitude() == null)  branch.setLatitude(existing.getLatitude());
        if (branch.getLongitude() == null) branch.setLongitude(existing.getLongitude());

        return ResponseEntity.ok(branchService.saveBranch(branch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
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