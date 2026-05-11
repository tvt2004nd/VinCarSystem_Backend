package com.vin.VinSystem.Notification.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.DeviceTokenRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Service.MailService;
import com.vin.VinSystem.Notification.Entity.Notification;
import com.vin.VinSystem.Notification.Repository.NotificationRepository;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
private final MailService mailService;

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;
    private final JavaMailSender         mailSender;
    private final SimpMessagingTemplate  messagingTemplate;
    private final FcmService             fcmService;
    private final DeviceTokenRepository  deviceTokenRepository;

    public NotificationService(NotificationRepository notificationRepository,
                                UserRepository userRepository,
                                JavaMailSender mailSender,
                                SimpMessagingTemplate messagingTemplate,
                                MailService mailService,
                                FcmService fcmService,
                                DeviceTokenRepository deviceTokenRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository         = userRepository;
        this.mailSender             = mailSender;
        this.messagingTemplate      = messagingTemplate;
        this.mailService = mailService;
        this.fcmService = fcmService;
        this.deviceTokenRepository = deviceTokenRepository;
    }

    // ── Core ─────────────────────────────────────────────────────────────────

    @Transactional
    public Notification createNotification(User user, String title, String message,
                                           String type, String referenceId) {
        Notification n = new Notification();
        n.setUser(user); n.setTitle(title); n.setMessage(message);
        n.setType(type); n.setReferenceId(referenceId); n.setIsRead(false);
        Notification saved = notificationRepository.save(n);
        pushRealtime(user.getUserId(), saved);
        log.info("[Notify] saved userId={} type={}", user.getUserId(), type);
        return saved;
    }

    @Transactional
    public void createNotification(Long userId, String title, String message,
                                   String type, String referenceId) {
        userRepository.findById(userId).ifPresentOrElse(
                user -> createNotification(user, title, message, type, referenceId),
                () -> log.warn("[Notify] userId={} không tồn tại", userId));
    }

    private void pushRealtime(Long userId, Notification n) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("notificationId", String.valueOf(n.getNotificationId()));
            data.put("type",           n.getType());
            data.put("referenceId",    n.getReferenceId());

            // 1. WebSocket Push (For Web/Active Mobile)
            messagingTemplate.convertAndSend("/topic/notifications/" + userId, n);

            // 2. FCM Push (For Background Mobile)
            deviceTokenRepository.findByUser_UserId(userId).forEach(token -> {
                fcmService.sendPushNotification(token.getFcmToken(), n.getTitle(), n.getMessage(), data);
            });

        } catch (Exception e) {
            log.error("[Notify] Push failed userId={} err={}", userId, e.getMessage());
        }
    }

    // ── Chat ─────────────────────────────────────────────────────────────────

    @Transactional
    public void notifyStaff(Long staffUserId, String title, String message, String referenceId) {
        createNotification(staffUserId, title, message, "CHAT_ASSIGNED", referenceId);
    }

    // ── Xe sẵn sàng ──────────────────────────────────────────────────────────

    @Transactional
    public void notifyCarReady(User customer, Long depositId, String carName, String branchName) {
        String title = "Xe của bạn đã sẵn sàng!";
        String msg   = String.format("Xe %s đã có mặt tại %s. Mã đặt cọc: #%d.", carName, branchName, depositId);
        createNotification(customer, title, msg, "CAR_READY", String.valueOf(depositId));
        sendEmail(customer.getEmail(), title, String.format(
                "Kính gửi %s,\n\nXe %s đã sẵn sàng tại %s.\nMã đặt cọc: #%d\n\n" +
                "Mang theo CMND/CCCD bản gốc.\nGiờ làm việc: 8:00-18:00 (T2-CN)\nHotline: 1800 2656\n\nTrân trọng,\nVinFast Vietnam",
                customer.getName(), carName, branchName, depositId));
    }

    // ── COMPLETED: Hợp đồng mua xe + PDF ─────────────────────────────────────

    @Transactional
    public void notifyDepositCompleted(User customer, Long depositId,
                                       String carName, double totalAmount,
                                       byte[] contractPdf) {
        String title = "Đơn đặt cọc hoàn tất";
        String msg   = String.format(
                "Giao dịch xe %s (mã #%d) đã hoàn tất. Hợp đồng đã được gửi qua email.",
                carName, depositId);
        createNotification(customer, title, msg, "DEPOSIT_COMPLETED", String.valueOf(depositId));

        String body = String.format(
                "Kính gửi %s,\n\nĐơn đặt cọc #%d cho xe %s đã hoàn tất.\n" +
                "Tổng chi phí lăn bánh: %,.0f đ\n\n" +
                "Hợp đồng mua xe đính kèm trong email này.\n\nHotline: 1800 2656\n\nTrân trọng,\nVinFast Vietnam",
                customer.getName(), depositId, carName, totalAmount);

        if (contractPdf != null && contractPdf.length > 0)
            sendEmailWithPdf(customer.getEmail(), title, body, contractPdf, "HopDong_VF" + depositId + ".pdf");
        else
            sendEmail(customer.getEmail(), title, body);
    }

    @Transactional
    public void notifyDepositCompleted(User customer, Long depositId,
                                       String carName, double totalAmount) {
        notifyDepositCompleted(customer, depositId, carName, totalAmount, null);
    }

    // ── FULL_PAYMENT: Biên lai thanh toán + PDF ───────────────────────────────

    @Transactional
    public void notifyFullPaymentCompleted(User customer, Long depositId,
                                           Long paymentId, String carName,
                                           double amount, byte[] receiptPdf) {
        String title = "Xác nhận thanh toán hoàn tất";
        String msg   = String.format(
                "Bạn đã thanh toán thành công %,.0f đ cho xe %s (đơn #%d). Biên lai đã gửi qua email.",
                amount, carName, depositId);
        createNotification(customer, title, msg, "FULL_PAYMENT_COMPLETED", String.valueOf(depositId));

        String body = String.format(
                "Kính gửi %s,\n\nXác nhận thanh toán thành công phần còn lại cho xe %s.\n" +
                "Mã đặt cọc: #%d\nMã giao dịch: #%d\nSố tiền: %,.0f đ\n\n" +
                "Biên lai đính kèm trong email này.\n\nHotline: 1800 2656\n\nTrân trọng,\nVinFast Vietnam",
                customer.getName(), carName, depositId, paymentId, amount);

        if (receiptPdf != null && receiptPdf.length > 0)
            sendEmailWithPdf(customer.getEmail(), title, body, receiptPdf, "BienLai_VF" + paymentId + ".pdf");
        else
            sendEmail(customer.getEmail(), title, body);
    }

    // ── Email helpers ─────────────────────────────────────────────────────────

public void sendEmail(String to, String subject, String body) {
    if (to == null || to.isBlank()) { log.warn("[Notify] email rỗng"); return; }
    try {
        mailService.sendEmail(to, subject, body);
        log.info("[Notify] email sent to={}", to);
    } catch (Exception e) {
        log.error("[Notify] sendEmail failed to={} err={}", to, e.getMessage());
    }
}

// Mới — dùng SendGrid
public void sendEmailWithPdf(String to, String subject, String body,
                              byte[] pdfBytes, String filename) {
    if (to == null || to.isBlank()) { log.warn("[Notify] email rỗng"); return; }
    try {
        mailService.sendEmailWithPdf(to, subject, body, pdfBytes, filename);
        log.info("[Notify] email+pdf sent to={} file={}", to, filename);
    } catch (Exception e) {
        log.error("[Notify] sendEmailWithPdf failed to={} err={}", to, e.getMessage());
        mailService.sendEmail(to, subject, body); // fallback
    }
}

    // ── Query ────────────────────────────────────────────────────────────────

    public List<Notification> getUnreadByUser(Long userId) {
        return notificationRepository.findByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getAllByUser(Long userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true); n.setReadAt(Instant.now()); notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> list =
                notificationRepository.findByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        list.forEach(n -> { n.setIsRead(true); n.setReadAt(Instant.now()); });
        notificationRepository.saveAll(list);
    }
}