package com.vin.VinSystem.Branch.Service;

import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Repository.BranchRepository;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;
     @Autowired
    private DepositRepository depositRepository;
        @Autowired
    private StaffRepository staffRepository;

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    /**
     * lat/lng đã được frontend tính toán qua VietMap place/v4
     * Backend chỉ validate và lưu — không geocode nữa
     */
    public Branch saveBranch(Branch branch) {

        if (branch.getLocation() == null || branch.getLocation().isBlank()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống");
        }

        return branchRepository.save(branch);
    }

public void deleteBranch(Long id) {
    if (!branchRepository.existsById(id)) {
        throw new RuntimeException("Branch not found");
    }
    // Kiểm tra còn deposit không
    if (depositRepository.existsByBranch_BranchId(id)) {
        throw new IllegalArgumentException("Chi nhánh đang có đơn đặt cọc, không thể xóa");
    }
    // Kiểm tra còn staff không
    if (staffRepository.existsByBranch_BranchId(id)) {
        throw new IllegalArgumentException("Chi nhánh đang có nhân viên, không thể xóa");
    }
    branchRepository.deleteById(id);
}
}