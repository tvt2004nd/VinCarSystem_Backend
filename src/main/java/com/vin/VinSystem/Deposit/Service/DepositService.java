package com.vin.VinSystem.Deposit.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Branch.Entity.Branch;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Deposit.DTO.DepositAdminResponse;
import com.vin.VinSystem.Deposit.DTO.DepositBranchResponse;
import com.vin.VinSystem.Deposit.DTO.DepositResponse;
import com.vin.VinSystem.Deposit.DTO.DepositStatsResponse;
import com.vin.VinSystem.Deposit.Entity.Deposit;
import com.vin.VinSystem.Deposit.Repository.DepositRepository;
import com.vin.VinSystem.Notification.Service.ContractPdfService;
import com.vin.VinSystem.Notification.Service.NotificationService;
import com.vin.VinSystem.Payment.Entity.Payment;
import com.vin.VinSystem.Payment.Repository.PaymentRepository;
import com.vin.VinSystem.Payment.Service.PaymentService;

@Service
public class DepositService {

    private static final Logger log     = LoggerFactory.getLogger(DepositService.class);
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final DepositRepository   depositRepository;
    private final UserRepository      userRepository;
    private final StaffRepository     staffRepository;
    private final PaymentRepository   paymentRepository;
    private final NotificationService notificationService;
    private final ContractPdfService  contractPdfService;

    public DepositService(DepositRepository depositRepository,
                          UserRepository userRepository,
                          StaffRepository staffRepository,
                          PaymentRepository paymentRepository,
                          NotificationService notificationService,
                          ContractPdfService contractPdfService) {
        this.depositRepository   = depositRepository;
        this.userRepository      = userRepository;
        this.staffRepository     = staffRepository;
        this.paymentRepository   = paymentRepository;
        this.notificationService = notificationService;
        this.contractPdfService  = contractPdfService;
    }

    private LocalDateTime nowVN() { return LocalDateTime.now(VN_ZONE); }

    // =========================================================
    // FLOW 1: ONLINE
    // =========================================================

    @Transactional
    public Deposit createOnlineDeposit(Deposit deposit, String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateCarOnSale(deposit.getCar());
        validateNoDuplicate(deposit.getCar(), customer);

        deposit.setCustomer(customer);
        deposit.setDepositType("ONLINE");
        deposit.setStatus("PENDING");
        deposit.setDepositDate(nowVN());

        Deposit saved = depositRepository.save(deposit);
        log.info("[Deposit] createOnlineDeposit depositId={} customerId={}",
                 saved.getDepositId(), customer.getUserId());
        return saved;
    }

    // =========================================================
    // FLOW 2: OFFLINE
    // =========================================================

    @Transactional
    public Deposit createOfflineDeposit(Deposit deposit, Long customerId,
                                        String paymentMethod, String staffUsername) {
        Staff staff = staffRepository.findByUser_Username(staffUsername)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Branch branch = staff.getBranch();
        if (branch == null) throw new RuntimeException("Staff chưa được gán chi nhánh.");

        validateCarOnSale(deposit.getCar());

        deposit.setCustomer(customer);
        deposit.setBranch(branch);
        deposit.setCreatedByStaff(staff.getUser());
        deposit.setDepositType("OFFLINE");
        deposit.setDepositDate(nowVN());

        if (List.of("CASH", "BANK_TRANSFER").contains(paymentMethod)) {
            deposit.setStatus("APPROVED");
            deposit.setApprovedAt(nowVN());
            Deposit saved = depositRepository.save(deposit);
            createPaymentRecord(saved, paymentMethod, PaymentService.STATUS_COMPLETED,
                    "DEPOSIT", saved.getDepositAmount());
            log.info("[Deposit] offline APPROVED depositId={}", saved.getDepositId());

            // Gửi hợp đồng đặt cọc ngay khi APPROVED (CASH/BANK_TRANSFER)
            sendDepositContractAsync(saved);
            return saved;

        } else if ("VNPAY".equals(paymentMethod)) {
            deposit.setStatus("PENDING");
            Deposit saved = depositRepository.save(deposit);
            createPaymentRecord(saved, "VNPAY", PaymentService.STATUS_PENDING,
                    "DEPOSIT", saved.getDepositAmount());
            log.info("[Deposit] offline PENDING (VNPay) depositId={}", saved.getDepositId());
            return saved;

        } else {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ: " + paymentMethod);
        }
    }

    // =========================================================
    // PAYMENT CALLBACK
    // =========================================================

    /**
     * PENDING → APPROVED sau khi thanh toán cọc thành công (VNPay callback).
     * Sau khi APPROVED: tạo PDF hợp đồng đặt cọc và gửi email cho khách.
     */
    @Transactional
    public Deposit markPaid(Long depositId) {
        Deposit deposit = getOrThrow(depositId);
        String  current = deposit.getStatus();

        if ("APPROVED".equals(current)) {
            log.info("[Deposit] markPaid — depositId={} đã APPROVED, skip", depositId);
            return deposit;
        }
        if (!"PENDING".equals(current)) {
            log.warn("[Deposit] markPaid — depositId={} status='{}', skip", depositId, current);
            return deposit;
        }

        deposit.setStatus("APPROVED");
        deposit.setApprovedAt(nowVN());
        paymentRepository.findByDeposit_DepositId(depositId).stream()
                .filter(p -> PaymentService.STATUS_PENDING.equals(p.getPaymentStatus()))
                .forEach(p -> {
                    p.setPaymentStatus(PaymentService.STATUS_COMPLETED);
                    paymentRepository.save(p);
                });

        Deposit saved = depositRepository.save(deposit);
        log.info("[Deposit] markPaid depositId={} PENDING → APPROVED ✅", depositId);

        // Gửi hợp đồng đặt cọc sau khi APPROVED
        sendDepositContractAsync(saved);

        return saved;
    }

    @Transactional
    public Deposit markFailed(Long depositId) {
        Deposit deposit = getOrThrow(depositId);
        String  current = deposit.getStatus();
        if ("CANCELLED".equals(current)) return deposit;
        if (!"PENDING".equals(current))  return deposit;

        deposit.setStatus("CANCELLED");
        paymentRepository.findByDeposit_DepositId(depositId).stream()
                .filter(p -> PaymentService.STATUS_PENDING.equals(p.getPaymentStatus()))
                .forEach(p -> {
                    p.setPaymentStatus(PaymentService.STATUS_FAILED);
                    paymentRepository.save(p);
                });
        return depositRepository.save(deposit);
    }

    // =========================================================
    // STAFF / ADMIN: TRẠNG THÁI ĐƠN
    // =========================================================

    @Transactional
    public Deposit markReady(Long depositId, String staffUsername) {
        Deposit deposit = getOrThrow(depositId);
        if (!"APPROVED".equals(deposit.getStatus()))
            throw new RuntimeException("Chỉ chuyển READY khi đang APPROVED. Hiện: " + deposit.getStatus());

        deposit.setStatus("READY");
        Deposit saved = depositRepository.save(deposit);

        String carName    = deposit.getCar()    != null ? deposit.getCar().getCarName()       : "—";
        String branchName = deposit.getBranch() != null ? deposit.getBranch().getBranchName() : "showroom";
        notificationService.notifyCarReady(deposit.getCustomer(), depositId, carName, branchName);

        log.info("[Deposit] markReady depositId={} by={}", depositId, staffUsername);
        return saved;
    }

    /**
     * READY → COMPLETED
     * Tạo PDF hợp đồng mua xe chính thức + gửi email kèm PDF.
     */
    @Transactional
    public CompletedResult markCompleted(Long depositId, BigDecimal onRoadTotal,
                                         String paymentMethod, String staffUsername) {
        Deposit deposit = getOrThrow(depositId);

        if (!"READY".equals(deposit.getStatus()))
            throw new RuntimeException("Chỉ hoàn thành khi đang READY. Hiện: " + deposit.getStatus());

        BigDecimal depositPaid     = deposit.getDepositAmount() != null
                                     ? deposit.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal remainingAmount = onRoadTotal.subtract(depositPaid).max(BigDecimal.ZERO);

        deposit.setStatus("COMPLETED");
        deposit.setOnRoadTotal(onRoadTotal);
        deposit.setRemainingAmount(remainingAmount);
        deposit.setCompletedAt(nowVN());
        Deposit saved = depositRepository.save(deposit);

        Long paymentId = null;
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            String payStatus = "VNPAY".equals(paymentMethod)
                    ? PaymentService.STATUS_PENDING
                    : PaymentService.STATUS_COMPLETED;
            Payment p = createPaymentRecord(saved, paymentMethod, payStatus,
                    "FULL_PAYMENT", remainingAmount);
            paymentId = p.getPaymentId();
        }

        String carName    = deposit.getCar()    != null ? deposit.getCar().getCarName()       : "—";
        String branchName = deposit.getBranch() != null ? deposit.getBranch().getBranchName() : "—";

        // Tạo PDF hợp đồng mua xe chính thức
        byte[] contractPdf = null;
        try {
            contractPdf = contractPdfService.generatePurchaseContract(
                    depositId,
                    deposit.getCustomer() != null ? deposit.getCustomer().getName()  : "—",
                    deposit.getCustomer() != null ? deposit.getCustomer().getEmail() : null,
                    carName,
                    branchName,
                    depositPaid,
                    onRoadTotal,
                    remainingAmount,
                    saved.getCompletedAt()
            );
            log.info("[Deposit] PDF hợp đồng mua xe created depositId={} size={}B",
                     depositId, contractPdf.length);
        } catch (Exception e) {
            log.error("[Deposit] tạo PDF hợp đồng mua xe thất bại depositId={} err={}",
                      depositId, e.getMessage());
        }

        try {
            notificationService.notifyDepositCompleted(
                    deposit.getCustomer(), depositId, carName,
                    onRoadTotal.doubleValue(), contractPdf);
        } catch (Exception e) {
            log.error("[Deposit] gửi notification COMPLETED thất bại depositId={} err={}",
                      depositId, e.getMessage());
        }

        log.info("[Deposit] markCompleted depositId={} onRoadTotal={} remaining={} method={} paymentId={} by={}",
                 depositId, onRoadTotal, remainingAmount, paymentMethod, paymentId, staffUsername);

        return new CompletedResult(saved, remainingAmount, paymentId);
    }

    public record CompletedResult(Deposit deposit, BigDecimal remainingAmount, Long paymentId) {}

    // =========================================================
    // HỦY ĐƠN
    // =========================================================

    @Transactional
    public Deposit cancelByCustomer(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Deposit deposit = getOrThrow(id);
        if (!deposit.getCustomer().getUserId().equals(user.getUserId()))
            throw new RuntimeException("Bạn không có quyền hủy đơn này.");
        if (!"PENDING".equals(deposit.getStatus()))
            throw new RuntimeException("Chỉ có thể hủy đơn đang ở trạng thái PENDING.");
        deposit.setStatus("CANCELLED");
        return depositRepository.save(deposit);
    }

    @Transactional
    public Deposit cancelByStaff(Long id, String staffUsername) {
        Staff staff = staffRepository.findByUser_Username(staffUsername)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        Deposit deposit = getOrThrow(id);
        Branch staffBranch = staff.getBranch();
        if (staffBranch == null ||
            !staffBranch.getBranchId().equals(deposit.getBranch().getBranchId()))
            throw new RuntimeException("Bạn không có quyền hủy đơn thuộc chi nhánh khác.");
        if ("CANCELLED".equals(deposit.getStatus()))
            throw new RuntimeException("Đơn đã bị hủy trước đó.");
        deposit.setStatus("CANCELLED");
        return depositRepository.save(deposit);
    }

    @Transactional
    public Deposit cancelByAdmin(Long id) {
        Deposit deposit = getOrThrow(id);
        if ("CANCELLED".equals(deposit.getStatus()))
            throw new RuntimeException("Đơn đã bị hủy trước đó.");
        deposit.setStatus("CANCELLED");
        return depositRepository.save(deposit);
    }

    // =========================================================
    // QUERY
    // =========================================================

    public DepositResponse getDepositById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Deposit deposit = getOrThrow(id);
        if (!deposit.getCustomer().getUserId().equals(user.getUserId()))
            throw new RuntimeException("Bạn không có quyền xem đơn này.");
        return new DepositResponse(deposit);
    }

    public List<DepositResponse> getMyDeposits(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return depositRepository.findAllByCustomerFull(user.getUserId())
                .stream()
                .map(DepositResponse::new)
                .toList();
    }

    public List<DepositAdminResponse> getAllDeposits() {
        return depositRepository.findAll()
                .stream()
                .map(DepositAdminResponse::new)
                .toList();
    }

    public List<DepositBranchResponse> getDepositsByBranch(Long branchId) {
        return depositRepository.findByBranch_BranchId(branchId)
                .stream()
                .map(DepositBranchResponse::new)
                .toList();
    }

    // =========================================================
    // STATS
    // =========================================================

    public DepositStatsResponse getUserStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Object[] overall = depositRepository.getStatsByUser(user.getUserId());
        long total = 0;
        BigDecimal amount = BigDecimal.ZERO;
        if (overall != null) {
            if (overall.length > 0) total  = safeToBigDecimal(overall[0]).longValue();
            if (overall.length > 1) amount = safeToBigDecimal(overall[1]);
        }
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] row : depositRepository.countByStatusByUser(user.getUserId()))
            countMap.put(row[0].toString(), safeToBigDecimal(row[1]).longValue());
        return new DepositStatsResponse(total, amount, countMap, null);
    }

    public DepositStatsResponse getAdminStats() {
        Object[] overall = depositRepository.getOverallStats();
        long total        = safeToBigDecimal(overall[0]).longValue();
        BigDecimal amount = safeToBigDecimal(overall[1]);
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] row : depositRepository.countByStatus())
            countMap.put(row[0].toString(), safeToBigDecimal(row[1]).longValue());
        Map<String, BigDecimal> amountMap = new HashMap<>();
        for (Object[] row : depositRepository.sumAmountByStatus())
            amountMap.put(row[0].toString(), safeToBigDecimal(row[1]));
        return new DepositStatsResponse(total, amount, countMap, amountMap);
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    /**
     * Tạo PDF hợp đồng đặt cọc và gửi email sau khi deposit APPROVED.
     * Fire-and-forget — lỗi PDF không làm fail transaction chính.
     */
    private void sendDepositContractAsync(Deposit deposit) {
        try {
            String carName    = deposit.getCar()    != null ? deposit.getCar().getCarName()       : "—";
            String branchName = deposit.getBranch() != null ? deposit.getBranch().getBranchName() : "—";
            String hotline    = deposit.getBranch() != null
                                && deposit.getBranch().getContactInfo() != null
                                ? deposit.getBranch().getContactInfo() : "1800 2656";
            User   customer   = deposit.getCustomer();

            byte[] depositContractPdf = contractPdfService.generateDepositContract(
                    deposit.getDepositId(),
                    customer != null ? customer.getName()        : "—",
                    customer != null ? customer.getEmail()       : null,
                    customer != null ? customer.getPhoneNumber() : null,
                    carName,
                    branchName,
                    hotline,
                    deposit.getDepositAmount(),
                    deposit.getOnRoadTotal(),   // có thể null nếu chưa tính lăn bánh
                    deposit.getDepositDate()
            );

            if (customer != null && customer.getEmail() != null) {
                String title = "Xác nhận đặt cọc xe " + carName;
                String body  = String.format(
                        "Kính gửi %s,\n\n" +
                        "Đặt cọc xe %s (mã #%d) đã được xác nhận thành công.\n\n" +
                        "Số tiền cọc: %,.0f đ\n\n" +
                        "Hợp đồng đặt cọc đính kèm trong email này.\n\n" +
                        "Lưu ý: Bạn có thể hủy trong vòng 7 ngày làm việc để được hoàn tiền 100%%.\n\n" +
                        "Hotline: %s\n\nTrân trọng,\nVinFast Vietnam",
                        customer.getName(), carName, deposit.getDepositId(),
                        deposit.getDepositAmount() != null ? deposit.getDepositAmount().doubleValue() : 0,
                        hotline);

                notificationService.sendEmailWithPdf(
                        customer.getEmail(),
                        title,
                        body,
                        depositContractPdf,
                        "HopDongDatCoc_VF" + deposit.getDepositId() + ".pdf"
                );

                // In-app notification
                notificationService.createNotification(
                        customer,
                        "✅ Đặt cọc xe " + carName + " thành công!",
                        "Hợp đồng đặt cọc #" + deposit.getDepositId() + " đã được gửi qua email. " +
                        "Bạn có 7 ngày làm việc để hủy và được hoàn tiền 100%.",
                        "DEPOSIT_APPROVED",
                        String.valueOf(deposit.getDepositId())
                );

                log.info("[Deposit] sendDepositContractAsync OK depositId={}", deposit.getDepositId());
            }
        } catch (Exception e) {
            log.error("[Deposit] sendDepositContractAsync FAILED depositId={} err={}",
                      deposit.getDepositId(), e.getMessage());
            // Không re-throw — transaction markPaid/createOfflineDeposit không bị ảnh hưởng
        }
    }

    private Deposit getOrThrow(Long id) {
        return depositRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deposit not found: " + id));
    }

    private void validateCarOnSale(Car car) {
        String status = car.getStatus();
        if ("UNAVAILABLE".equals(status) || "INACTIVE".equals(status))
            throw new RuntimeException("Xe \"" + car.getCarName() + "\" hiện đã ngừng bán.");
        if ("COMING_SOON".equals(status))
            throw new RuntimeException("Xe \"" + car.getCarName() + "\" chưa mở bán.");
    }

    private void validateNoDuplicate(Car car, User customer) {
        Optional<Deposit> existing = depositRepository
                .findFirstByCarAndCustomerAndStatusIn(car, customer, List.of("PENDING", "APPROVED"));
        if (existing.isEmpty()) return;
        Deposit dup = existing.get();
        if ("APPROVED".equals(dup.getStatus()))
            throw new RuntimeException("Bạn đã đặt cọc thành công xe này (đơn #" + dup.getDepositId() + ").");
        throw new RuntimeException("Bạn đang có đơn chờ TT (đơn #" + dup.getDepositId() + "). Thanh toán hoặc hủy trước.");
    }

    private Payment createPaymentRecord(Deposit deposit, String method, String status,
                                        String paymentType, BigDecimal amount) {
        Payment p = new Payment();
        p.setDeposit(deposit);
        p.setAmount(amount);
        p.setPaymentDate(nowVN());
        p.setPaymentMethod(method);
        p.setPaymentStatus(status);
        p.setPaymentType(paymentType);
        p.setProvider(List.of("CASH", "BANK_TRANSFER").contains(method) ? "DIRECT" : method);
        Payment saved = paymentRepository.save(p);
        log.info("[Deposit] payment id={} depositId={} type={} method={} status={} amount={}",
                 saved.getPaymentId(), deposit.getDepositId(), paymentType, method, status, amount);
        return saved;
    }

    private BigDecimal safeToBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        if (val instanceof Object[] arr) return safeToBigDecimal(arr[0]);
        return BigDecimal.ZERO;
    }
}