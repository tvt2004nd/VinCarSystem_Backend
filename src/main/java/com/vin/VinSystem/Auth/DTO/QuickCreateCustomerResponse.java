package com.vin.VinSystem.Auth.DTO;

public class QuickCreateCustomerResponse {

    private Long   userId;
    private String name;
    private String username;
    private String email;
    private String role;

    // Thông tin đăng nhập — hiển thị 1 lần cho staff đọc cho khách
    private String tempPassword;
    private String phone;

    public QuickCreateCustomerResponse(Long userId, String name, String username,
                                       String email, String role,
                                       String tempPassword, String phone) {
        this.userId      = userId;
        this.name        = name;
        this.username    = username;
        this.email       = email;
        this.role        = role;
        this.tempPassword = tempPassword;
        this.phone       = phone;
    }

    public Long   getUserId()       { return userId; }
    public String getName()         { return name; }
    public String getUsername()     { return username; }
    public String getEmail()        { return email; }
    public String getRole()         { return role; }
    public String getTempPassword() { return tempPassword; }
    public String getPhone()        { return phone; }
}