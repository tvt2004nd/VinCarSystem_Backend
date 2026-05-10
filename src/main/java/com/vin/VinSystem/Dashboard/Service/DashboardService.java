package com.vin.VinSystem.Dashboard.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vin.VinSystem.Appointment.Repository.AppointmentRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Dashboard.DTO.RecentTransactionDTO;
import com.vin.VinSystem.Dashboard.DTO.RevenueDTO;
import com.vin.VinSystem.Dashboard.DTO.StatusDTO;
import com.vin.VinSystem.Dashboard.Response.DashboardResponse;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;

@Service
public class DashboardService {

    private static final List<String> REVENUE_STATUSES = List.of("APPROVED", "COMPLETED");

    private final CarRepository         carRepo;
    private final DepositRepository     depositRepo;
    private final AppointmentRepository appointmentRepo;

    public DashboardService(CarRepository carRepo,
                            DepositRepository depositRepo,
                            AppointmentRepository appointmentRepo) {
        this.carRepo         = carRepo;
        this.depositRepo     = depositRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public DashboardResponse getDashboard(String type, String status,
                                          String search, String category,
                                          Integer year, Integer month) {
        DashboardResponse res = new DashboardResponse();
        int currentYear = LocalDate.now().getYear();
        int filterYear  = (year != null) ? year : currentYear;

        // ── 1. KPI tổng quan ─────────────────────────────────────────────
        res.setTotalCars(carRepo.count());
        res.setTotalDeposits(depositRepo.count());
        res.setTotalAppointments(appointmentRepo.count());

        BigDecimal totalRevenue = depositRepo.findAll().stream()
                .filter(d -> REVENUE_STATUSES.contains(d.getStatus()))
                .map(d -> d.getDepositAmount() != null ? d.getDepositAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        res.setTotalRevenue(totalRevenue);

        // ── 2. Biểu đồ doanh thu theo type + year/month ──────────────────
        List<Object[]> raw;

        if ("year".equals(type)) {
            // Toàn bộ các năm
            raw = depositRepo.getRevenueByYear();
            res.setRevenue(raw.stream()
                .map(o -> new RevenueDTO(
                    "Năm " + o[0],
                    toBigDecimal(o[1])
                )).toList());

        } else if ("quarter".equals(type)) {
            // Theo quý của năm được chọn
            raw = depositRepo.getRevenueByQuarterAndYear(filterYear);
            res.setRevenue(raw.stream()
                .map(o -> new RevenueDTO(
                    "Q" + o[0] + "/" + filterYear,
                    toBigDecimal(o[1])
                )).toList());

        } else if ("day".equals(type) && month != null) {
            // Theo ngày trong tháng/năm
            raw = depositRepo.getRevenueByDay(filterYear, month);
            res.setRevenue(raw.stream()
                .map(o -> new RevenueDTO(
                    "Ngày " + o[0],
                    toBigDecimal(o[1])
                )).toList());

        } else {
            // Mặc định: theo tháng của năm được chọn
            raw = depositRepo.getRevenueByMonthAndYear(filterYear);
            String[] months = {"Th1","Th2","Th3","Th4","Th5","Th6",
                               "Th7","Th8","Th9","Th10","Th11","Th12"};
            res.setRevenue(raw.stream()
                .map(o -> {
                    int m = ((Number) o[0]).intValue();
                    return new RevenueDTO(
                        (m >= 1 && m <= 12 ? months[m-1] : String.valueOf(m)) + "/" + filterYear,
                        toBigDecimal(o[1])
                    );
                }).toList());
        }

        // ── 3. Deposit status chart ───────────────────────────────────────
        res.setDepositStatus(depositRepo.countByStatus().stream()
            .map(o -> new StatusDTO((String) o[0], ((Number) o[1]).longValue()))
            .toList());

        // ── 4. Giao dịch gần đây ─────────────────────────────────────────
        List<RecentTransactionDTO> combined = new ArrayList<>();

        if (category == null || "ALL".equalsIgnoreCase(category) || "DEPOSIT".equalsIgnoreCase(category)) {
            combined.addAll(depositRepo.findAll().stream()
                .filter(d -> status == null || "ALL".equalsIgnoreCase(status)
                          || d.getStatus().equalsIgnoreCase(status))
                .filter(d -> matchesSearch(
                    d.getCustomer() != null ? d.getCustomer().getName() : null,
                    d.getCar() != null ? d.getCar().getCarName() : null, search))
                .map(d -> new RecentTransactionDTO(
                    "DEPOSIT",
                    d.getCustomer() != null ? d.getCustomer().getName() : "—",
                    d.getCar() != null ? d.getCar().getCarName() : "—",
                    d.getDepositDate(),
                    d.getDepositAmount(),
                    d.getStatus()))
                .toList());
        }

        if (category == null || "ALL".equalsIgnoreCase(category) || "APPOINTMENT".equalsIgnoreCase(category)) {
            combined.addAll(appointmentRepo.findAll().stream()
                .filter(a -> status == null || "ALL".equalsIgnoreCase(status)
                          || a.getStatus().equalsIgnoreCase(status))
                .filter(a -> matchesSearch(
                    a.getCustomer() != null ? a.getCustomer().getName() : null,
                    a.getCar() != null ? a.getCar().getCarName() : null, search))
                .map(a -> new RecentTransactionDTO(
                    "APPOINTMENT",
                    a.getCustomer() != null ? a.getCustomer().getName() : "—",
                    a.getCar() != null ? a.getCar().getCarName() : "—",
                    a.getAppointmentDate(),
                    null,
                    a.getStatus()))
                .toList());
        }

        res.setRecentTransactions(combined.stream()
            .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
            .limit(10)
            .collect(Collectors.toList()));

        return res;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal bd) return bd;
        if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }

    private boolean matchesSearch(String customer, String car, String search) {
        if (search == null || search.trim().isEmpty()) return true;
        String s = search.toLowerCase();
        return (customer != null && customer.toLowerCase().contains(s))
            || (car != null && car.toLowerCase().contains(s));
    }
}