package com.vin.VinSystem.Notification.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Notification.Entity.Notification;
import com.vin.VinSystem.Notification.Service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository      userRepository;

    public NotificationController(NotificationService notificationService,
                                   UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository      = userRepository;
    }

    /** Lấy tất cả notification của user hiện tại */
    @GetMapping
    public List<Notification> getMyNotifications(Principal principal) {
        User user = getUser(principal);
        return notificationService.getAllByUser(user.getUserId());
    }

    /** Lấy notification chưa đọc */
    @GetMapping("/unread")
    public List<Notification> getUnread(Principal principal) {
        User user = getUser(principal);
        return notificationService.getUnreadByUser(user.getUserId());
    }

    /** Đánh dấu 1 notification đã đọc */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đã đọc"));
    }

    /** Đánh dấu tất cả đã đọc */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllRead(Principal principal) {
        User user = getUser(principal);
        notificationService.markAllRead(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "Đã đánh dấu tất cả đã đọc"));
    }

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}