package com.vin.VinSystem.Branch.Repository;

import com.vin.VinSystem.Branch.Entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository cho Branch
 * Cung cấp các thao tác CRUD với bảng branch
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
}