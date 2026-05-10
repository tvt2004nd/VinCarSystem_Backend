package com.vin.VinSystem.Dashboard.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vin.VinSystem.Appointment.Entity.Appointment;
import com.vin.VinSystem.Appointment.Repository.AppointmentRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Dashboard.Response.DashboardResponse;
import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;

class DashboardServiceTest {

    CarRepository         carRepo         = mock(CarRepository.class);
    DepositRepository     depositRepo     = mock(DepositRepository.class);
    AppointmentRepository appointmentRepo = mock(AppointmentRepository.class);
    DashboardService      service;

    @BeforeEach
    void setUp() {
        service = new DashboardService(carRepo, depositRepo, appointmentRepo);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Deposit mockDeposit(String status, BigDecimal amount) {
        Deposit d = new Deposit();
        d.setStatus(status);
        d.setDepositAmount(amount);
        d.setDepositDate(LocalDateTime.now());
        return d;
    }

    private Appointment mockAppointment(String status) {
        Appointment a = new Appointment();
        a.setStatus(status);
        a.setAppointmentDate(LocalDateTime.now());
        return a;
    }

    private void stubDefaults() {
        when(carRepo.count()).thenReturn(5L);
        when(depositRepo.count()).thenReturn(10L);
        when(appointmentRepo.count()).thenReturn(3L);
        when(depositRepo.findAll()).thenReturn(List.of());
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(appointmentRepo.findAll()).thenReturn(List.of());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());
    }

    // ── KPI tổng quan ─────────────────────────────────────────────────────

    @Test
    void getDashboard_kpis_success() {
        stubDefaults();

        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);

        assertThat(res.getTotalCars()).isEqualTo(5L);
        assertThat(res.getTotalDeposits()).isEqualTo(10L);
        assertThat(res.getTotalAppointments()).isEqualTo(3L);
        assertThat(res.getTotalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getDashboard_totalRevenue_onlyApprovedAndCompleted() {
        when(carRepo.count()).thenReturn(0L);
        when(depositRepo.count()).thenReturn(3L);
        when(appointmentRepo.count()).thenReturn(0L);
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(appointmentRepo.findAll()).thenReturn(List.of());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());

        List<Deposit> deposits = List.of(
            mockDeposit("APPROVED",   new BigDecimal("100000000")),
            mockDeposit("COMPLETED",  new BigDecimal("200000000")),
            mockDeposit("PENDING",    new BigDecimal("50000000")),  // bị bỏ qua
            mockDeposit("CANCELLED",  new BigDecimal("30000000"))   // bị bỏ qua
        );
        when(depositRepo.findAll()).thenReturn(deposits);

        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);

        assertThat(res.getTotalRevenue()).isEqualByComparingTo("300000000");
    }

    // ── Revenue chart — theo type ─────────────────────────────────────────

    @Test
    void getDashboard_revenueByMonth_defaultYear() {
        stubDefaults();

        List<Object[]> raw = new ArrayList<>();
        raw.add(new Object[]{3, new BigDecimal("100000000")});
        raw.add(new Object[]{4, new BigDecimal("200000000")});
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(raw);

        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);

        assertThat(res.getRevenue()).hasSize(2);
        assertThat(res.getRevenue().get(0).getLabel()).contains("Th3");
        assertThat(res.getRevenue().get(0).getValue()).isEqualByComparingTo("100000000");
    }

    @Test
    void getDashboard_revenueByYear() {
        stubDefaults();

        List<Object[]> raw = new ArrayList<>();
        raw.add(new Object[]{2025, new BigDecimal("500000000")});
        raw.add(new Object[]{2026, new BigDecimal("900000000")});
        when(depositRepo.getRevenueByYear()).thenReturn(raw);

        DashboardResponse res = service.getDashboard("year", null, null, null, null, null);

        assertThat(res.getRevenue()).hasSize(2);
        assertThat(res.getRevenue().get(0).getLabel()).isEqualTo("Năm 2025");
    }

    @Test
    void getDashboard_revenueByQuarter() {
        stubDefaults();

        List<Object[]> raw = new ArrayList<>();
        raw.add(new Object[]{1, new BigDecimal("300000000")});
        raw.add(new Object[]{2, new BigDecimal("400000000")});
        when(depositRepo.getRevenueByQuarterAndYear(2026)).thenReturn(raw);

        DashboardResponse res = service.getDashboard("quarter", null, null, null, 2026, null);

        assertThat(res.getRevenue()).hasSize(2);
        assertThat(res.getRevenue().get(0).getLabel()).isEqualTo("Q1/2026");
    }

    @Test
    void getDashboard_revenueByDay() {
        stubDefaults();

        List<Object[]> raw = new ArrayList<>();
        raw.add(new Object[]{1,  new BigDecimal("10000000")});
        raw.add(new Object[]{15, new BigDecimal("20000000")});
        when(depositRepo.getRevenueByDay(2026, 4)).thenReturn(raw);

        DashboardResponse res = service.getDashboard("day", null, null, null, 2026, 4);

        assertThat(res.getRevenue()).hasSize(2);
        assertThat(res.getRevenue().get(0).getLabel()).isEqualTo("Ngày 1");
    }

    @Test
    void getDashboard_revenueByDay_noMonth_fallsBackToMonthly() {
        stubDefaults();
        // type=day nhưng month=null → fallback theo tháng
        DashboardResponse res = service.getDashboard("day", null, null, null, 2026, null);

        verify(depositRepo).getRevenueByMonthAndYear(2026);
    }

    // ── Deposit status chart ──────────────────────────────────────────────

    @Test
    void getDashboard_depositStatusChart() {
        stubDefaults();

        List<Object[]> statusRows = new ArrayList<>();
        statusRows.add(new Object[]{"PENDING",   3L});
        statusRows.add(new Object[]{"COMPLETED", 7L});
        when(depositRepo.countByStatus()).thenReturn(statusRows);

        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);

        assertThat(res.getDepositStatus()).hasSize(2);
        assertThat(res.getDepositStatus().get(0).getName()).isEqualTo("PENDING");
        assertThat(res.getDepositStatus().get(0).getValue()).isEqualTo(3L);
    }

    // ── Recent transactions ───────────────────────────────────────────────

    @Test
    void getDashboard_recentTransactions_combined() {
        when(carRepo.count()).thenReturn(0L);
        when(depositRepo.count()).thenReturn(1L);
        when(appointmentRepo.count()).thenReturn(1L);
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());

        Deposit deposit = mockDeposit("APPROVED", new BigDecimal("50000000"));
        Appointment appt = mockAppointment("PENDING");

        when(depositRepo.findAll()).thenReturn(List.of(deposit));
        when(appointmentRepo.findAll()).thenReturn(List.of(appt));

        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);

        assertThat(res.getRecentTransactions()).hasSize(2);
        assertThat(res.getRecentTransactions())
            .extracting("type")
            .containsExactlyInAnyOrder("DEPOSIT", "APPOINTMENT");
    }

    @Test
    void getDashboard_recentTransactions_filterByStatus() {
        when(carRepo.count()).thenReturn(0L);
        when(depositRepo.count()).thenReturn(2L);
        when(appointmentRepo.count()).thenReturn(0L);
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());
        when(appointmentRepo.findAll()).thenReturn(List.of());

        when(depositRepo.findAll()).thenReturn(List.of(
            mockDeposit("APPROVED",  new BigDecimal("100000000")),
            mockDeposit("CANCELLED", new BigDecimal("50000000"))
        ));

        DashboardResponse res = service.getDashboard("month", "APPROVED", null, "DEPOSIT", null, null);

        assertThat(res.getRecentTransactions()).hasSize(1);
        assertThat(res.getRecentTransactions().get(0).getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void getDashboard_recentTransactions_filterByCategory_depositOnly() {
        when(carRepo.count()).thenReturn(0L);
        when(depositRepo.count()).thenReturn(1L);
        when(appointmentRepo.count()).thenReturn(1L);
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());

        when(depositRepo.findAll()).thenReturn(List.of(mockDeposit("APPROVED", new BigDecimal("50000000"))));
        when(appointmentRepo.findAll()).thenReturn(List.of(mockAppointment("PENDING")));

        DashboardResponse res = service.getDashboard("month", null, null, "DEPOSIT", null, null);

        assertThat(res.getRecentTransactions()).hasSize(1);
        assertThat(res.getRecentTransactions().get(0).getType()).isEqualTo("DEPOSIT");
    }

    @Test
    void getDashboard_recentTransactions_limitTo10() {
        when(carRepo.count()).thenReturn(0L);
        when(depositRepo.count()).thenReturn(15L);
        when(appointmentRepo.count()).thenReturn(0L);
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());
        when(appointmentRepo.findAll()).thenReturn(List.of());

        List<Deposit> many = new ArrayList<>();
        for (int i = 0; i < 15; i++) many.add(mockDeposit("APPROVED", new BigDecimal("10000000")));
        when(depositRepo.findAll()).thenReturn(many);

        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);

        assertThat(res.getRecentTransactions()).hasSize(10);
    }

    // ── Search filter ──────────────────────────────────────────────────────

    @Test
    void getDashboard_searchFilter_matchesCustomerName() {
        when(carRepo.count()).thenReturn(0L);
        when(depositRepo.count()).thenReturn(2L);
        when(appointmentRepo.count()).thenReturn(0L);
        when(depositRepo.countByStatus()).thenReturn(new ArrayList<>());
        when(depositRepo.getRevenueByMonthAndYear(anyInt())).thenReturn(new ArrayList<>());
        when(appointmentRepo.findAll()).thenReturn(List.of());

        // Deposit có customer name
        Deposit d1 = mockDeposit("APPROVED", new BigDecimal("50000000"));
        // d1 không có customer → matchesSearch trả true khi search null
        // d2 có customer name
        Deposit d2 = mockDeposit("APPROVED", new BigDecimal("50000000"));

        when(depositRepo.findAll()).thenReturn(List.of(d1, d2));

        // search = null → trả tất cả
        DashboardResponse res = service.getDashboard("month", null, null, null, null, null);
        assertThat(res.getRecentTransactions()).hasSize(2);
    }
}