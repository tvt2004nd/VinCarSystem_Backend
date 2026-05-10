package com.vin.VinSystem.Auth.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Auth.Entity.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /** Lấy danh sách nhân viên theo chi nhánh */
    List<Staff> findByBranch_BranchId(Long branchId);
boolean existsByBranch_BranchId(Long branchId);
    /**
     * ✅ Tìm Staff theo userId của User liên kết
     * Dùng khi frontend gửi userId thay vì staffId
     */
    Optional<Staff> findByUser_UserId(Long userId);
    @Query("""
    SELECT s
    FROM Staff s
    WHERE s.position='Tư vấn bán hàng'
    AND s.workStatus='ACTIVE'
""")
List<Staff> findConsultants();
  List<Staff> findByPositionAndWorkStatus(String position, String workStatus);
  Staff findByUserUserId(Long userId);
  Optional<Staff> findByUser_Username(String username);
  
List<Staff> findByWorkStatus(String workStatus);
List<Staff> findByBranch_BranchIdAndWorkStatus(Long branchId, String workStatus);
}