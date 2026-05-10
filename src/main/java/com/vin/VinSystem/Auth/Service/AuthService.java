package com.vin.VinSystem.Auth.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vin.VinSystem.Auth.DTO.ChangePasswordRequest;
import com.vin.VinSystem.Auth.DTO.LoginRequest;
import com.vin.VinSystem.Auth.DTO.LoginResponse;
import com.vin.VinSystem.Auth.DTO.RegisterRequest;
import com.vin.VinSystem.Auth.DTO.UserResponse;
import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Security.JwtUtil;

@Service
public class AuthService {

    @Autowired private UserRepository     userRepository;
    @Autowired private RoleRepository     roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private StaffRepository    staffRepository;
    @Autowired private PasswordEncoder    passwordEncoder;
    @Autowired private JwtUtil            jwtUtil;
    @Autowired private MailService        mailService;

    // OTP có thời hạn 5 phút
    private final Map<String, String>  otpStorage    = new HashMap<>();
    private final Map<String, Long>    otpExpiry     = new HashMap<>();
    private static final long          OTP_TTL_MS    = 5 * 60 * 1000L;
    private final Map<String, RegisterRequest> registerStorage = new HashMap<>();
    private final Map<String, String> registerOtp = new HashMap<>();
    private final Map<String, Long> registerExpiry = new HashMap<>();

    // ── Regex patterns ──────────────────────────────
    private static final String USERNAME_RE = "^[a-zA-Z0-9_]{3,30}$";
    private static final String EMAIL_RE    = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String PASSWORD_RE = "^(?=.*[A-Z])(?=.*[0-9]).{6,50}$";

    // ================= REGISTER =================

    @Transactional
    public UserResponse register(RegisterRequest request) {

        // ── Validate input ──────────────────────────
        if (!StringUtils.hasText(request.getUsername()))
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        if (!request.getUsername().matches(USERNAME_RE))
            throw new IllegalArgumentException("Tên đăng nhập 3–30 ký tự, chỉ dùng chữ/số/dấu gạch dưới");

        if (!StringUtils.hasText(request.getEmail()))
            throw new IllegalArgumentException("Email không được để trống");
        if (!request.getEmail().matches(EMAIL_RE))
            throw new IllegalArgumentException("Email không hợp lệ");

        if (!StringUtils.hasText(request.getPassword()))
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        if (!request.getPassword().matches(PASSWORD_RE))
            throw new IllegalArgumentException("Mật khẩu 6–50 ký tự, cần ít nhất 1 chữ hoa và 1 chữ số");

        if (!StringUtils.hasText(request.getName()))
            throw new IllegalArgumentException("Họ và tên không được để trống");
        if (request.getName().trim().length() < 2)
            throw new IllegalArgumentException("Họ và tên ít nhất 2 ký tự");

        // ── Duplicate check ─────────────────────────
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setName(request.getName().trim());

        User savedUser = userRepository.save(user);

        Role role = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return new UserResponse(savedUser.getUserId(), savedUser.getUsername(),
                savedUser.getEmail(), savedUser.getName(), "CUSTOMER");
    }

    // gửi OTP khi đăng ký, lưu tạm request và OTP vào memory để verify sau khi user nhập OTP đúng thì mới tạo account
    public void sendRegisterOtp(RegisterRequest request) {

        if (!StringUtils.hasText(request.getUsername()))
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");

        if (!request.getUsername().matches(USERNAME_RE))
            throw new IllegalArgumentException("Username không hợp lệ");

        if (!StringUtils.hasText(request.getEmail()))
            throw new IllegalArgumentException("Email không được để trống");

        if (!request.getEmail().matches(EMAIL_RE))
            throw new IllegalArgumentException("Email không hợp lệ");

        if (!StringUtils.hasText(request.getPassword()))
            throw new IllegalArgumentException("Mật khẩu không được để trống");

        if (!request.getPassword().matches(PASSWORD_RE))
            throw new IllegalArgumentException("Mật khẩu yếu");

        if (!StringUtils.hasText(request.getName()))
            throw new IllegalArgumentException("Tên không được để trống");

        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username đã tồn tại");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email đã tồn tại");

        String email = request.getEmail().trim().toLowerCase();

        String otp = mailService.sendOtp(email);

        registerStorage.put(email, request);
        registerOtp.put(email, otp);
        registerExpiry.put(email, System.currentTimeMillis() + OTP_TTL_MS);
    }

    // verify OTP khi đăng ký, nếu đúng thì tạo account, nếu sai hoặc hết hạn thì xoá tạm request và OTP đã lưu
    @Transactional
    public UserResponse verifyRegisterOtp(String email, String otp) {

        if (!StringUtils.hasText(email) || !StringUtils.hasText(otp))
            throw new IllegalArgumentException("Email và OTP không được để trống");

        email = email.trim().toLowerCase();

        Long expiry = registerExpiry.get(email);

        if (expiry == null || System.currentTimeMillis() > expiry) {

            registerOtp.remove(email);
            registerStorage.remove(email);
            registerExpiry.remove(email);

            throw new RuntimeException("OTP đã hết hạn");
        }

        String savedOtp = registerOtp.get(email);

        if (savedOtp == null || !savedOtp.equals(otp))
            throw new RuntimeException("OTP không đúng");

        RegisterRequest request = registerStorage.get(email);

        if (request == null)
            throw new RuntimeException("Không tìm thấy dữ liệu đăng ký");

        User user = new User();

        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setName(request.getName().trim());

        User savedUser = userRepository.save(user);

        Role role = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        UserRole userRole = new UserRole();

        userRole.setUser(savedUser);
        userRole.setRole(role);

        userRoleRepository.save(userRole);

        registerOtp.remove(email);
        registerStorage.remove(email);
        registerExpiry.remove(email);

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getName(),
                "CUSTOMER");
    }

    // ================= LOGIN =================

    @Transactional
    public LoginResponse login(LoginRequest request) {

        // ── Validate input ──────────────────────────
        if (!StringUtils.hasText(request.getUsername()))
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        if (!StringUtils.hasText(request.getPassword()))
            throw new IllegalArgumentException("Mật khẩu không được để trống");

        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng");
        // NOTE: dùng cùng message để tránh enumerate username

        String role = userRoleRepository.findByUser(user)
                .stream()
                .map(ur -> ur.getRole().getRoleName())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"));

        String token      = jwtUtil.generateToken(user.getUsername());
        Long   customerId = null, staffId = null;
        String staffPosition = null;
        Long branchId = null;

        if ("CUSTOMER".equals(role)) {
            customerId = user.getUserId();
        }
        if ("STAFF".equals(role) || "MANAGER".equals(role)) {
            Staff staff = staffRepository.findByUserUserId(user.getUserId());
            if (staff != null) {
                staffId       = staff.getUserId();
                staffPosition = staff.getPosition();
                branchId = (staff.getBranch() != null)
                        ? staff.getBranch().getBranchId()
                        : null;
            }
        }

        return new LoginResponse(
                token,
                user.getUsername(),
                role,
                customerId,
                staffId,
                staffPosition,
                branchId
        );
    }

    // ================= PROFILE =================

    @Transactional
    public UserResponse getUserByUsername(String username) {
        if (!StringUtils.hasText(username))
            throw new IllegalArgumentException("Username không hợp lệ");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = userRoleRepository.findByUser(user)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getRole().getRoleName();

        return new UserResponse(user.getUserId(), user.getUsername(),
                user.getEmail(), user.getName(), role);
    }

    // ================= FORGOT PASSWORD =================

    public void forgotPassword(String email) {
        if (!StringUtils.hasText(email))
            throw new IllegalArgumentException("Email không được để trống");
        if (!email.matches(EMAIL_RE))
            throw new IllegalArgumentException("Email không hợp lệ");

        userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email chưa được đăng ký"));

        String otp = mailService.sendOtp(email.trim().toLowerCase());
        otpStorage.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + OTP_TTL_MS);
    }

    public void verifyOtp(String email, String otp) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(otp))
            throw new IllegalArgumentException("Email và OTP không được để trống");

        // Kiểm tra hết hạn
        Long expiry = otpExpiry.get(email);
        if (expiry == null || System.currentTimeMillis() > expiry) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            throw new RuntimeException("OTP đã hết hạn, vui lòng gửi lại");
        }

        String savedOtp = otpStorage.get(email);
        if (savedOtp == null || !savedOtp.equals(otp.trim()))
            throw new RuntimeException("OTP không đúng");
    }

    public void resetPassword(String email, String newPassword) {
        if (!StringUtils.hasText(email))
            throw new IllegalArgumentException("Email không được để trống");
        if (!StringUtils.hasText(newPassword))
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        if (!newPassword.matches(PASSWORD_RE))
            throw new IllegalArgumentException("Mật khẩu 6–50 ký tự, cần ít nhất 1 chữ hoa và 1 chữ số");

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xoá OTP sau khi đổi mật khẩu thành công
        otpStorage.remove(email);
        otpExpiry.remove(email);
    }

    // ================= CHANGE PASSWORD =================

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        if (!StringUtils.hasText(request.getOldPassword()))
            throw new IllegalArgumentException("Mật khẩu cũ không được để trống");
        if (!StringUtils.hasText(request.getNewPassword()))
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        if (!request.getNewPassword().matches(PASSWORD_RE))
            throw new IllegalArgumentException("Mật khẩu mới 6–50 ký tự, cần ít nhất 1 chữ hoa và 1 chữ số");
        if (request.getOldPassword().equals(request.getNewPassword()))
            throw new IllegalArgumentException("Mật khẩu mới không được trùng mật khẩu cũ");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new RuntimeException("Mật khẩu cũ không đúng");

        // Double-check với bcrypt để tránh trường hợp hash khác nhau
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new RuntimeException("Mật khẩu mới không được trùng mật khẩu cũ");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}