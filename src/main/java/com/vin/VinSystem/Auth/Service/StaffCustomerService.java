package com.vin.VinSystem.Auth.Service;

import java.text.Normalizer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vin.VinSystem.Auth.DTO.QuickCreateCustomerRequest;
import com.vin.VinSystem.Auth.DTO.QuickCreateCustomerResponse;
import com.vin.VinSystem.Auth.DTO.UserResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;

@Service
public class StaffCustomerService {

    private static final String PHONE_RE = "^(0|\\+84)[3-9]\\d{8}$";
    private static final String EMAIL_RE = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    @Autowired private UserRepository     userRepository;
    @Autowired private RoleRepository     roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private PasswordEncoder    passwordEncoder;
    @Autowired private MailService        mailService;

    // ── Tìm khách theo SĐT ──────────────────────────────────────────────────
    public UserResponse findByPhone(String phone) {
        if (!StringUtils.hasText(phone))
            throw new IllegalArgumentException("Số điện thoại không được để trống");

        String normalized = phone.trim().replaceAll("\\s", "");
        if (!normalized.matches(PHONE_RE))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (vd: 0912345678)");

        User user = userRepository.findFirstByPhoneNumber(normalized)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        String role = userRoleRepository.findByUser(user)
                .stream().findFirst()
                .map(ur -> ur.getRole().getRoleName())
                .orElse("CUSTOMER");

        return new UserResponse(
                user.getUserId(), user.getUsername(),
                user.getEmail(), user.getName(), role);
    }

    // ── Tạo tài khoản CUSTOMER nhanh ────────────────────────────────────────
    @Transactional
    public QuickCreateCustomerResponse quickCreate(QuickCreateCustomerRequest req) {

        if (!StringUtils.hasText(req.getName()) || req.getName().trim().length() < 2)
            throw new IllegalArgumentException("Họ và tên ít nhất 2 ký tự");

        if (!StringUtils.hasText(req.getPhone()))
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        String phone = req.getPhone().trim().replaceAll("\\s", "");
        if (!phone.matches(PHONE_RE))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");

        if (userRepository.findFirstByPhoneNumber(phone).isPresent())
            throw new RuntimeException("Số điện thoại này đã có tài khoản trong hệ thống");

        String email = null;
        if (StringUtils.hasText(req.getEmail())) {
            email = req.getEmail().trim().toLowerCase();
            if (!email.matches(EMAIL_RE))
                throw new IllegalArgumentException("Email không hợp lệ");
            if (userRepository.existsByEmail(email))
                throw new RuntimeException("Email này đã được đăng ký");
        }

        // ── Username đẹp: slug từ tên + 4 số cuối SĐT ──────────────────────
        // VD: "Nguyễn Văn An" + "0912345678" → "nguyen_van_an_5678"
        String nameSlug    = slugify(req.getName().trim());
        String phoneSuffix = phone.substring(phone.length() - 4);
        String baseUsername = nameSlug + "_" + phoneSuffix;

        // Đảm bảo username không trùng
        String username = baseUsername;
        int attempt = 0;
        while (userRepository.existsByUsername(username)) {
            attempt++;
            username = baseUsername + "_" + attempt;
        }

        // ── Password dễ đọc: "Vin@" + 4 số cuối SĐT + 4 ký tự random HOA ──
        // VD: "Vin@5678AB3F" — đủ mạnh, dễ đọc cho staff, đủ điều kiện regex
        String rawPassword = "Vin@" + phoneSuffix
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setName(req.getName().trim());
        user.setPhoneNumber(phone);
        if (email != null) user.setEmail(email);

        User saved = userRepository.save(user);

        Role role = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));
        UserRole userRole = new UserRole();
        userRole.setUser(saved);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        if (email != null) {
            try { mailService.sendAccountInfo(email, username, rawPassword); }
            catch (Exception ignored) { }
        }

        // Trả về credentials để staff đọc cho khách
        return new QuickCreateCustomerResponse(
                saved.getUserId(), saved.getName(), saved.getUsername(),
                saved.getEmail(), "CUSTOMER", rawPassword, phone);
    }

    // ── Chuyển tên tiếng Việt → slug ASCII ──────────────────────────────────
    // "Nguyễn Văn An" → "nguyen_van_an"
    private String slugify(String input) {
        String out = Normalizer.normalize(input, Normalizer.Form.NFD);
        out = out.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        out = out.replace("đ", "d").replace("Đ", "d");
        out = out.toLowerCase().trim().replaceAll("\\s+", "_");
        out = out.replaceAll("[^a-z0-9_]", "");
        if (out.length() > 20) out = out.substring(0, 20);
        return out;
    }
}