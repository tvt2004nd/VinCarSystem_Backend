package com.vin.VinSystem.Auth.DTO;

public class QuickCreateCustomerRequest {
    private String name;
    private String phone;
    private String email; // tuỳ chọn

    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setName(String v)  { this.name = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setEmail(String v) { this.email = v; }
    
}