package com.vin.VinSystem.Branch.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Branch.Repository.BranchRepository;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;

class BranchServiceTest {

    BranchRepository branchRepository = mock(BranchRepository.class);
    BranchService    service;
 DepositRepository depositRepository = mock(DepositRepository.class);
 StaffRepository   staffRepository   = mock(StaffRepository.class);
    @BeforeEach
    void setUp() {
        service = new BranchService();
        ReflectionTestUtils.setField(service, "branchRepository",  branchRepository);
        ReflectionTestUtils.setField(service, "depositRepository", depositRepository);
        ReflectionTestUtils.setField(service, "staffRepository",   staffRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Branch mockBranch() {
        Branch b = new Branch();
        b.setBranchId(1L);
        b.setBranchName("Chi nhánh HN");
        b.setLocation("Bến Xe Yên Nghĩa, Hà Nội");
        b.setLatitude(20.94);
        b.setLongitude(105.74);
        return b;
    }

    // ── getAllBranches ─────────────────────────────────────────────────────

    @Test
    void getAllBranches_success() {
        when(branchRepository.findAll()).thenReturn(List.of(mockBranch()));

        List<Branch> result = service.getAllBranches();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBranchName()).isEqualTo("Chi nhánh HN");
    }

    // ── getBranchById ──────────────────────────────────────────────────────

    @Test
    void getBranchById_found() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch()));

        Optional<Branch> result = service.getBranchById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getBranchId()).isEqualTo(1L);
    }

    @Test
    void getBranchById_notFound() {
        when(branchRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.getBranchById(99L)).isEmpty();
    }

    // ── saveBranch ────────────────────────────────────────────────────────

    @Test
    void saveBranch_success() {
        Branch b = mockBranch();
        when(branchRepository.save(b)).thenReturn(b);

        Branch result = service.saveBranch(b);

        assertThat(result.getBranchName()).isEqualTo("Chi nhánh HN");
        verify(branchRepository).save(b);
    }

    @Test
    void saveBranch_fail_emptyLocation() {
        Branch b = mockBranch();
        b.setLocation("");

        assertThatThrownBy(() -> service.saveBranch(b))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Địa chỉ không được để trống");
    }

    @Test
    void saveBranch_fail_nullLocation() {
        Branch b = mockBranch();
        b.setLocation(null);

        assertThatThrownBy(() -> service.saveBranch(b))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Địa chỉ không được để trống");
    }

    // ── deleteBranch ──────────────────────────────────────────────────────

 @Test
    void deleteBranch_success() {
        when(branchRepository.existsById(1L)).thenReturn(true);
        when(depositRepository.existsByBranch_BranchId(1L)).thenReturn(false);
        when(staffRepository.existsByBranch_BranchId(1L)).thenReturn(false);

        assertThatNoException().isThrownBy(() -> service.deleteBranch(1L));
        verify(branchRepository).deleteById(1L);
    }

    @Test
    void deleteBranch_fail_notFound() {
        when(branchRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteBranch(99L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Branch not found");
    }
}