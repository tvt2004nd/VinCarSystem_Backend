package com.vin.VinSystem.Auth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Auth.DTO.ChangePasswordRequest;
import com.vin.VinSystem.Auth.DTO.ForgotPasswordRequest;
import com.vin.VinSystem.Auth.DTO.LoginRequest;
import com.vin.VinSystem.Auth.DTO.LoginResponse;
import com.vin.VinSystem.Auth.DTO.RegisterRequest;
import com.vin.VinSystem.Auth.DTO.ResetPasswordRequest;
import com.vin.VinSystem.Auth.DTO.UserResponse;
import com.vin.VinSystem.Auth.DTO.VerifyOtpRequest;
import com.vin.VinSystem.Auth.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    // SEND OTP REGISTER
@PostMapping("/register/send-otp")
public ResponseEntity<String> sendRegisterOtp(@RequestBody RegisterRequest request) {

    authService.sendRegisterOtp(request);

    return ResponseEntity.ok("OTP đã được gửi tới email");
}
// VERIFY OTP REGISTER
@PostMapping("/register/verify")
public UserResponse verifyRegisterOtp(@RequestBody VerifyOtpRequest request) {

    return authService.verifyRegisterOtp(
            request.getEmail(),
            request.getOtp()
    );
}

    // LOGIN → trả token
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

    LoginResponse response = authService.login(request);

    return ResponseEntity.ok(response);
}

    // PROFILE → lấy từ token
    @GetMapping("/profile")
    public UserResponse getProfile(Authentication authentication) {

        String username = authentication.getName();

        return authService.getUserByUsername(username);
    }
    // quên pass
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP đã được gửi");
    }
    //nhap otp
    @PostMapping("/verify-otp")
    public void verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp());
    }
    // reset pass
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(
            request.getEmail(),
            request.getNewPassword()
        );
    }
    @PostMapping("/change-password")
public ResponseEntity<String> changePassword(
        Authentication authentication,
        @RequestBody ChangePasswordRequest request) {

    String username = authentication.getName();
    authService.changePassword(username, request);

    return ResponseEntity.ok("Đổi mật khẩu thành công");
}
}
