package com.vin.VinSystem.Auth.Controller;

import com.vin.VinSystem.Common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.vin.VinSystem.Auth.DTO.*;
import com.vin.VinSystem.Auth.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request), "Đăng ký thành công");
    }

    // SEND OTP REGISTER
    @PostMapping("/register/send-otp")
    public ApiResponse<Void> sendRegisterOtp(@RequestBody RegisterRequest request) {
        authService.sendRegisterOtp(request);
        return ApiResponse.success(null, "OTP đã được gửi tới email");
    }

    // VERIFY OTP REGISTER
    @PostMapping("/register/verify")
    public ApiResponse<UserResponse> verifyRegisterOtp(@RequestBody VerifyOtpRequest request) {
        UserResponse response = authService.verifyRegisterOtp(
                request.getEmail(),
                request.getOtp()
        );
        return ApiResponse.success(response, "Xác thực thành công");
    }

    // LOGIN → trả token
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response, "Đăng nhập thành công");
    }

    // PROFILE → lấy từ token
    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse response = authService.getUserByUsername(username);
        return ApiResponse.success(response);
    }

    // QUÊN PASS
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ApiResponse.success(null, "OTP đã được gửi");
    }

    // NHẬP OTP
    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp());
        return ApiResponse.success(null, "Xác thực OTP thành công");
    }

    // RESET PASS
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(
            request.getEmail(),
            request.getNewPassword()
        );
        return ApiResponse.success(null, "Cập nhật mật khẩu thành công");
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        authService.changePassword(username, request);
        return ApiResponse.success(null, "Đổi mật khẩu thành công");
    }
}
