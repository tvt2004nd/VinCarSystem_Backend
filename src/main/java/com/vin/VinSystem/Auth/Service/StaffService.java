package com.vin.VinSystem.Auth.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Auth.DTO.StaffRequest;
import com.vin.VinSystem.Auth.DTO.StaffResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Repository.BranchRepository;

@Service
public class StaffService {

    private static final String POSITION_MANAGER = "Quản lý chi nhánh";

    @Autowired private UserRepository userRepository;
    @Autowired private StaffRepository staffRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private BranchRepository branchRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── QUERIES ──────────────────────────────────────────────────────────────

    public List<StaffResponse> getAllStaff() {
        return staffRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StaffResponse> getStaffByBranch(Long branchId) {
        return staffRepository.findByBranch_BranchId(branchId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Staff not found"));
        return toResponse(staff);
    }

    public List<StaffResponse> getActiveStaff() {
        return staffRepository.findByWorkStatus("ACTIVE").stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StaffResponse> getActiveStaffByBranch(Long branchId) {
        return staffRepository.findByBranch_BranchIdAndWorkStatus(branchId, "ACTIVE").stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── VALIDATION ───────────────────────────────────────────────────────────

    private void checkManagerPerBranch(Long branchId, String position, Long excludeUserId) {
        if (!POSITION_MANAGER.equals(position)) return;
        if (branchId == null) return;

        List<Staff> staffInBranch = staffRepository.findByBranch_BranchId(branchId);
        for (Staff s : staffInBranch) {
            if (excludeUserId != null && s.getUserId().equals(excludeUserId)) continue;
            if (POSITION_MANAGER.equals(s.getPosition())) {
                Branch branch = branchRepository.findById(branchId).orElse(null);
                String branchName = branch != null ? branch.getBranchName() : "này";
                throw new RuntimeException(
                    "Chi nhánh " + branchName +
                    " đã có Quản lý chi nhánh là \"" + s.getUser().getName() + "\"!"
                );
            }
        }
    }

    // ── COMMANDS ─────────────────────────────────────────────────────────────

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");

        checkManagerPerBranch(request.getBranchId(), request.getPosition(), null);

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        userRepository.save(user);

        Role staffRole = roleRepository.findByRoleName("STAFF")
                .orElseThrow(() -> new RuntimeException("Role STAFF not found"));
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(staffRole);
        userRoleRepository.save(userRole);

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
        }

        Staff staff = new Staff();
        staff.setUser(user);
        staff.setBranch(branch);
        staff.setPosition(request.getPosition());
        staff.setWorkStatus(request.getWorkStatus());
        staffRepository.save(staff);

        return toResponse(staff);
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        Staff staff = staffRepository.findByUser_UserId(id)
                .orElseGet(() -> staffRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Staff not found")));

        checkManagerPerBranch(request.getBranchId(), request.getPosition(), staff.getUserId());

        User user = staff.getUser();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        if (request.getPassword() != null && !request.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            staff.setBranch(branch);
        } else {
            staff.setBranch(null);
        }
        staff.setPosition(request.getPosition());
        staff.setWorkStatus(request.getWorkStatus());
        staffRepository.save(staff);

        return toResponse(staff);
    }

    @Transactional
    public void deleteStaff(Long userId) {
        Staff staff = staffRepository.findByUser_UserId(userId)
                .orElseGet(() -> staffRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Staff not found: " + userId)));

        User user = staff.getUser();
        user.setUsername("deleted_" + user.getUserId());
        user.setPassword("");
        userRepository.save(user);

        staff.setWorkStatus("INACTIVE");
        staffRepository.save(staff);
    }

    // ── MAPPING ──────────────────────────────────────────────────────────────

    private StaffResponse toResponse(Staff staff) {
        StaffResponse res = new StaffResponse();
        res.setUserId(staff.getUserId());
        res.setPosition(staff.getPosition());
        res.setWorkStatus(staff.getWorkStatus());

        if (staff.getUser() != null) {
            User user = staff.getUser();
            res.setName(user.getName());
            res.setUsername(user.getUsername());
            res.setEmail(user.getEmail());
            res.setPhoneNumber(user.getPhoneNumber());
            res.setAddress(user.getAddress());
            res.setAvatar(user.getAvatar()); // ← thêm dòng này

            List<UserRole> roles = userRoleRepository.findByUser_UserId(staff.getUserId());
            if (!roles.isEmpty())
                res.setRoleName(roles.get(0).getRole().getRoleName());
        }

        if (staff.getBranch() != null) {
            res.setBranchId(staff.getBranch().getBranchId());
            res.setBranchName(staff.getBranch().getBranchName());
        }

        return res;
    }
}