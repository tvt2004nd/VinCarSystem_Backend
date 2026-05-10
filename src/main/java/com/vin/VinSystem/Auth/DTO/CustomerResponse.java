package com.vin.VinSystem.Auth.DTO;

public class CustomerResponse {

    private Long userId;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;

    public CustomerResponse(Long userId,
                            String username,
                            String name,
                            String email,
                            String phoneNumber,
                            String address,
                            String avatar) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.avatar = avatar;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getAvatar() { return avatar; }
}
