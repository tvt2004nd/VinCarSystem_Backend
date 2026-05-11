package com.vin.VinSystem.Auth.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Auth.DTO.UserProfileResponse;
import com.vin.VinSystem.Auth.Entity.DeviceToken;
import com.vin.VinSystem.Auth.Repository.DeviceTokenRepository;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @PostMapping("/device-token")
    public ApiResponse<Void> registerDeviceToken(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        
        String fcmToken = body.get("fcmToken");
        String deviceType = body.getOrDefault("deviceType", "ANDROID");

        if (!StringUtils.hasText(fcmToken)) {
            throw new IllegalArgumentException("fcmToken is required");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        deviceTokenRepository.findByFcmToken(fcmToken).ifPresentOrElse(
            token -> {
                token.setUser(user);
                token.setDeviceType(deviceType);
                deviceTokenRepository.save(token);
            },
            () -> {
                deviceTokenRepository.save(new DeviceToken(user, fcmToken, deviceType));
            }
        );

        return ApiResponse.success(null, "Device token registered successfully");
    }

    private static final String PHONE_RE = "^(0|\\+84)[3-9]\\d{8}$";

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getProfile(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ApiResponse.success(new UserProfileResponse(user));
    }

    @PutMapping("/update")
    public ApiResponse<Void> updateProfile(
            @RequestBody UpdateProfileRequest body,
            Authentication authentication) {

        // ── Validate phone ──────────────────────────
        if (StringUtils.hasText(body.getPhoneNumber())) {
            String phone = body.getPhoneNumber().replaceAll("\\s", "");
            if (!phone.matches(PHONE_RE))
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (vd: 0912345678)");
        }

        // ── Validate address ────────────────────────
        if (StringUtils.hasText(body.getAddress()) && body.getAddress().trim().length() > 300)
            throw new IllegalArgumentException("Địa chỉ tối đa 300 ký tự");

        String username = authentication.getName();
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPhoneNumber(
            StringUtils.hasText(body.getPhoneNumber())
                ? body.getPhoneNumber().replaceAll("\\s", "")
                : null
        );
        user.setAddress(
            StringUtils.hasText(body.getAddress())
                ? body.getAddress().trim()
                : null
        );

        userRepository.save(user);

        return ApiResponse.success(null, "Cập nhật thành công");
    }

    // ── Inner DTO để tránh nhận thẳng Entity ──────
    public static class UpdateProfileRequest {
        private String phoneNumber;
        private String address;
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String v) { this.phoneNumber = v; }
        public String getAddress() { return address; }
        public void setAddress(String v) { this.address = v; }
    }
}