package com.vin.VinSystem.Auth.DTO;

public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private String name;
    private String role;

    public UserResponse(Long userId, String username, String email, String name, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public UserResponse() {}

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }
}