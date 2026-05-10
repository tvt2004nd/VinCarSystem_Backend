package com.vin.VinSystem.Deposit.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Deposit.Entity.Deposit;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findFirstByCarAndCustomerAndStatusIn(
            Car car, User customer, List<String> statuses);

    List<Deposit> findByCustomer_UserId(Long userId);

    List<Deposit> findByBranch_BranchId(Long branchId);

    @Query("SELECT d FROM Deposit d " +
           "LEFT JOIN FETCH d.car " +
           "LEFT JOIN FETCH d.branch " +
           "LEFT JOIN FETCH d.customer " +
           "WHERE d.customer.userId = :userId " +
           "ORDER BY d.depositDate DESC")
    List<Deposit> findAllByCustomerFull(@Param("userId") Long userId);

    // ── STATS ────────────────────────────────────────────────────────────

    @Query("SELECT COUNT(d), COALESCE(SUM(d.depositAmount),0) FROM Deposit d")
    Object[] getOverallStats();

    @Query("SELECT d.status, COUNT(d) FROM Deposit d GROUP BY d.status")
    List<Object[]> countByStatus();

    @Query("SELECT d.status, COALESCE(SUM(d.depositAmount),0) FROM Deposit d GROUP BY d.status")
    List<Object[]> sumAmountByStatus();

    @Query("SELECT COUNT(d), COALESCE(SUM(d.depositAmount),0) FROM Deposit d WHERE d.customer.userId = :userId")
    Object[] getStatsByUser(@Param("userId") Long userId);
boolean existsByBranch_BranchId(Long branchId);
    @Query("SELECT d.status, COUNT(d) FROM Deposit d WHERE d.customer.userId = :userId GROUP BY d.status")
    List<Object[]> countByStatusByUser(@Param("userId") Long userId);

    // ── REVENUE CHARTS ───────────────────────────────────────────────────
    // Tính cả APPROVED và COMPLETED

    // Theo tháng — toàn bộ
    @Query("""
        SELECT MONTH(d.depositDate), COALESCE(SUM(d.depositAmount),0)
        FROM Deposit d
        WHERE d.status IN ('APPROVED','COMPLETED')
        GROUP BY MONTH(d.depositDate)
        ORDER BY MONTH(d.depositDate)
    """)
    List<Object[]> getRevenueByMonth();

    // Theo tháng — lọc theo năm cụ thể
    @Query("""
        SELECT MONTH(d.depositDate), COALESCE(SUM(d.depositAmount),0)
        FROM Deposit d
        WHERE d.status IN ('APPROVED','COMPLETED')
          AND YEAR(d.depositDate) = :year
        GROUP BY MONTH(d.depositDate)
        ORDER BY MONTH(d.depositDate)
    """)
    List<Object[]> getRevenueByMonthAndYear(@Param("year") int year);

    // Theo quý — toàn bộ
    @Query("""
        SELECT FUNCTION('QUARTER', d.depositDate), COALESCE(SUM(d.depositAmount),0)
        FROM Deposit d
        WHERE d.status IN ('APPROVED','COMPLETED')
        GROUP BY FUNCTION('QUARTER', d.depositDate)
        ORDER BY FUNCTION('QUARTER', d.depositDate)
    """)
    List<Object[]> getRevenueByQuarter();

    // Theo quý — lọc theo năm
    @Query("""
        SELECT FUNCTION('QUARTER', d.depositDate), COALESCE(SUM(d.depositAmount),0)
        FROM Deposit d
        WHERE d.status IN ('APPROVED','COMPLETED')
          AND YEAR(d.depositDate) = :year
        GROUP BY FUNCTION('QUARTER', d.depositDate)
        ORDER BY FUNCTION('QUARTER', d.depositDate)
    """)
    List<Object[]> getRevenueByQuarterAndYear(@Param("year") int year);

    // Theo năm — toàn bộ
    @Query("""
        SELECT YEAR(d.depositDate), COALESCE(SUM(d.depositAmount),0)
        FROM Deposit d
        WHERE d.status IN ('APPROVED','COMPLETED')
        GROUP BY YEAR(d.depositDate)
        ORDER BY YEAR(d.depositDate)
    """)
    List<Object[]> getRevenueByYear();

    // Theo tháng cụ thể trong năm (cho báo cáo chi tiết 1 tháng)
    @Query("""
        SELECT DAY(d.depositDate), COALESCE(SUM(d.depositAmount),0)
        FROM Deposit d
        WHERE d.status IN ('APPROVED','COMPLETED')
          AND YEAR(d.depositDate) = :year
          AND MONTH(d.depositDate) = :month
        GROUP BY DAY(d.depositDate)
        ORDER BY DAY(d.depositDate)
    """)
    List<Object[]> getRevenueByDay(@Param("year") int year, @Param("month") int month);
}