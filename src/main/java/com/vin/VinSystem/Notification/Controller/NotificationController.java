package com.vin.VinSystem.Notification.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Common.ApiResponse;
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
    public ApiResponse<List<Notification>> getMyNotifications(Principal principal) {
        User user = getUser(principal);
        return ApiResponse.success(notificationService.getAllByUser(user.getUserId()));
    }

    /** Lấy notification chưa đọc */
    @GetMapping("/unread")
    public ApiResponse<List<Notification>> getUnread(Principal principal) {
        User user = getUser(principal);
        return ApiResponse.success(notificationService.getUnreadByUser(user.getUserId()));
    }

    /** Đánh dấu 1 notification đã đọc */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ApiResponse.success(null, "Đã đánh dấu đã đọc");
    }

    /** Đánh dấu tất cả đã đọc */
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllRead(Principal principal) {
        User user = getUser(principal);
        notificationService.markAllRead(user.getUserId());
        return ApiResponse.success(null, "Đã đánh dấu tất cả đã đọc");
    }

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}