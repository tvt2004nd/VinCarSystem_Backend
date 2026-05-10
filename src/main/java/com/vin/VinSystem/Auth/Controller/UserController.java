package com.vin.VinSystem.Auth.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Auth.DTO.UserProfileResponse;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private static final String PHONE_RE = "^(0|\\+84)[3-9]\\d{8}$";

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new UserProfileResponse(user));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest body,
            Authentication authentication) {

        // ── Validate phone ──────────────────────────
        if (StringUtils.hasText(body.getPhoneNumber())) {
            String phone = body.getPhoneNumber().replaceAll("\\s", "");
            if (!phone.matches(PHONE_RE))
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Số điện thoại không hợp lệ (vd: 0912345678)"));
        }

        // ── Validate address ────────────────────────
        if (StringUtils.hasText(body.getAddress()) && body.getAddress().trim().length() > 300)
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Địa chỉ tối đa 300 ký tự"));

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

        return ResponseEntity.ok(Map.of("message", "Cập nhật thành công"));
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