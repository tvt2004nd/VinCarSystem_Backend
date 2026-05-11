package com.vin.VinSystem.Deposit.Controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Branch.Repository.BranchRepository;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Deposit.DTO.DepositAdminResponse;
import com.vin.VinSystem.Deposit.DTO.DepositBranchResponse;
import com.vin.VinSystem.Deposit.DTO.DepositResponse;
import com.vin.VinSystem.Deposit.DTO.OfflineDepositRequest;
import com.vin.VinSystem.Deposit.DTO.OnlineDepositRequest;
import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Service.DepositService;
import com.vin.VinSystem.Deposit.Service.DepositService.CompletedResult;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/deposits")
@Validated
public class DepositController {

        private final DepositService depositService;
        private final CarRepository carRepository;
        private final BranchRepository branchRepository;

        public DepositController(DepositService depositService,
                                CarRepository carRepository,
                                BranchRepository branchRepository) {
            this.depositService   = depositService;
            this.carRepository    = carRepository;
            this.branchRepository = branchRepository;
        }

        // ─────────────────────────────────────────────────────────
        // FLOW 1: CUSTOMER ONLINE
        // ─────────────────────────────────────────────────────────

        @PostMapping("/online")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<DepositResponse> createOnlineDeposit(@Valid @RequestBody OnlineDepositRequest req,
                                                               Principal principal) {
            Car car = carRepository.findById(req.getCarId())
                    .orElseThrow(() -> new RuntimeException("Car not found: " + req.getCarId()));

            com.vin.VinSystem.Branch.Entity.Branch branch = branchRepository
                    .findById(req.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found: " + req.getBranchId()));

            Deposit deposit = new Deposit();
            deposit.setCar(car);
            deposit.setBranch(branch);
            deposit.setDepositAmount(req.getAmount());

            Deposit saved = depositService.createOnlineDeposit(deposit, principal.getName());
            return ApiResponse.success(new DepositResponse(saved), "Tạo đặt cọc thành công");
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
        public ApiResponse<DepositResponse> getDepositById(@PathVariable Long id, Principal principal) {
            return ApiResponse.success(depositService.getDepositById(id, principal.getName()));
        }

        @PutMapping("/{id}/cancel")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<DepositResponse> cancelByCustomer(@PathVariable Long id, Principal principal) {
            Deposit saved = depositService.cancelByCustomer(id, principal.getName());
            return ApiResponse.success(new DepositResponse(saved), "Hủy đặt cọc thành công");
        }

        @GetMapping("/my")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<List<DepositResponse>> getMyDeposits(Principal principal) {
            return ApiResponse.success(depositService.getMyDeposits(principal.getName()));
        }

        // ─────────────────────────────────────────────────────────
        // FLOW 2: STAFF OFFLINE
        // ─────────────────────────────────────────────────────────

        @PostMapping("/offline")
        @PreAuthorize("hasRole('STAFF')")
        public ApiResponse<Map<String, Object>> createOfflineDeposit(
                @Valid @RequestBody OfflineDepositRequest req,
                Principal principal) {

            Car car = carRepository.findById(req.getCarId())
                    .orElseThrow(() -> new RuntimeException("Car not found: " + req.getCarId()));

            Deposit deposit = new Deposit();
            deposit.setCar(car);
            deposit.setDepositAmount(req.getAmount());

            Deposit saved = depositService.createOfflineDeposit(
                    deposit, req.getCustomerId(), req.getPaymentMethod(), principal.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("depositId",     saved.getDepositId());
            response.put("status",        saved.getStatus());
            response.put("depositAmount", saved.getDepositAmount());
            response.put("depositType",   saved.getDepositType());

            return ApiResponse.success(response, "Tạo đặt cọc offline thành công");
        }

        @PutMapping("/{id}/cancel-staff")
        @PreAuthorize("hasRole('STAFF')")
        public ApiResponse<DepositResponse> cancelByStaff(@PathVariable Long id, Principal principal) {
            Deposit saved = depositService.cancelByStaff(id, principal.getName());
            return ApiResponse.success(new DepositResponse(saved), "Hủy đặt cọc thành công");
        }

        @GetMapping("/branch/{branchId}")
        @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
        public ApiResponse<List<DepositBranchResponse>> getDepositsByBranch(@PathVariable Long branchId) {
            return ApiResponse.success(depositService.getDepositsByBranch(branchId));
        }

        @PutMapping("/{id}/paid")
        public ApiResponse<DepositResponse> markPaid(@PathVariable Long id) {
            Deposit saved = depositService.markPaid(id);
            return ApiResponse.success(new DepositResponse(saved), "Cập nhật trạng thái thành công");
        }

        @PutMapping("/{id}/failed")
        public ApiResponse<DepositResponse> markFailed(@PathVariable Long id) {
            Deposit saved = depositService.markFailed(id);
            return ApiResponse.success(new DepositResponse(saved), "Cập nhật trạng thái thành công");
        }

        // ── STAFF / ADMIN: Xe sẵn sàng (APPROVED → READY) ───────────────────────

        @PutMapping("/{id}/ready")
        @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
        public ApiResponse<Map<String, Object>> markReady(@PathVariable Long id,
                                                         Principal principal) {
            Deposit saved = depositService.markReady(id, principal.getName());

            Map<String, Object> resp = new HashMap<>();
            resp.put("depositId",     saved.getDepositId());
            resp.put("status",        saved.getStatus());
            resp.put("customerName",  saved.getCustomer() != null ? saved.getCustomer().getName()        : null);
            resp.put("customerPhone", saved.getCustomer() != null ? saved.getCustomer().getPhoneNumber() : null);
            resp.put("customerEmail", saved.getCustomer() != null ? saved.getCustomer().getEmail()       : null);
            resp.put("carName",       saved.getCar()      != null ? saved.getCar().getCarName()          : null);
            resp.put("branchName",    saved.getBranch()   != null ? saved.getBranch().getBranchName()    : null);
            resp.put("message",       "Đã thông báo cho khách. Xe sẵn sàng tại showroom.");

            return ApiResponse.success(resp, "Cập nhật trạng thái thành công");
        }
@GetMapping("/my/stats")
@PreAuthorize("hasRole('CUSTOMER')")
public ApiResponse<Object> getMyStats(Principal principal) {
    return ApiResponse.success(depositService.getUserStats(principal.getName()));
}
        // ── STAFF / ADMIN: Hoàn thành (READY → COMPLETED) ────────────────────────

        @PutMapping("/{id}/complete")
        @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
        public ApiResponse<Map<String, Object>> markCompleted(
                @PathVariable Long id,
                @RequestBody Map<String, Object> body,
                Principal principal) {

            BigDecimal onRoadTotal   = new BigDecimal(body.get("onRoadTotal").toString());
            String     paymentMethod = (String) body.getOrDefault("paymentMethod", "CASH");

            CompletedResult result = depositService.markCompleted(
                    id, onRoadTotal, paymentMethod, principal.getName());

            Deposit saved = result.deposit();

            Map<String, Object> resp = new HashMap<>();
            resp.put("depositId",       saved.getDepositId());
            resp.put("status",          saved.getStatus());
            resp.put("onRoadTotal",     saved.getOnRoadTotal());
            resp.put("depositAmount",   saved.getDepositAmount());
            resp.put("remainingAmount", result.remainingAmount());
            resp.put("paymentMethod",   paymentMethod);
            resp.put("paymentId",       result.paymentId());
            resp.put("needVnpay",       "VNPAY".equals(paymentMethod) && result.paymentId() != null);
            resp.put("message",         "VNPAY".equals(paymentMethod) && result.paymentId() != null
                                        ? "Vui lòng tạo link VNPay để khách thanh toán phần còn lại."
                                        : "Đơn hoàn tất thành công!");

            return ApiResponse.success(resp, "Hoàn tất đơn thành công");
        }

        // ─────────────────────────────────────────────────────────
        // ADMIN
        // ─────────────────────────────────────────────────────────

        @GetMapping("/all")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<List<DepositAdminResponse>> getAllDeposits() {
            return ApiResponse.success(depositService.getAllDeposits());
        }

        @PutMapping("/{id}/cancel-admin")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<Deposit> cancelByAdmin(@PathVariable Long id) {
            return ApiResponse.success(depositService.cancelByAdmin(id), "Hủy đơn thành công");
        }
    }