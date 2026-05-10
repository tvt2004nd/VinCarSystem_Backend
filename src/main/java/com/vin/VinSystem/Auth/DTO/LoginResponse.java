package com.vin.VinSystem.Auth.DTO;

public class LoginResponse {

    private String token;
    private String type;
    private String username;
    public LoginResponse(String token, String username, String role, Long customerId, Long staffId,
            String staffPosition, Long branchId) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.role = role;
        this.customerId = customerId;
        this.staffId = staffId;
        this.staffPosition = staffPosition;
        this.branchId = branchId;
    }
    
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
    private String role;
    private Long customerId;
    private Long staffId;
    private String staffPosition;
    private Long branchId;

    // Constructors
    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.role = role;
    }

    public LoginResponse(String token, String username, String role, Long customerId) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.role = role;
        this.customerId = customerId;
    }

    public LoginResponse(String token, String type, String username, String role, Long customerId) {
        this.token = token;
        this.type = type;
        this.username = username;
        this.role = role;
        this.customerId = customerId;
    }

    public LoginResponse(String token, String username, String role,
                         Long customerId, Long staffId, String staffPosition) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.role = role;
        this.customerId = customerId;
        this.staffId = staffId;
        this.staffPosition = staffPosition;

    }

    // Getters
    public String getToken() { return token; }
    public String getType() { return type; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Long getCustomerId() { return customerId; }
    public Long getStaffId() { return staffId; }
    public String getStaffPosition() { return staffPosition; }

    // Setters
    public void setToken(String token) { this.token = token; }
    public void setType(String type) { this.type = type; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    public void setStaffPosition(String staffPosition) { this.staffPosition = staffPosition; }
}