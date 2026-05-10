package com.vin.VinSystem.Chat.DTO;

public class ChatSessionDTO {

    private Long sessionId;
    private String type;
    private String status;
    private Long customerId;
    private String customerName; // FIX: thêm tên khách
    private Long staffId;

    public ChatSessionDTO() {}

    public ChatSessionDTO(Long sessionId, String type, String status,
                          Long customerId, String customerName, Long staffId) {
        this.sessionId    = sessionId;
        this.type         = type;
        this.status       = status;
        this.customerId   = customerId;
        this.customerName = customerName;
        this.staffId      = staffId;
    }

    public Long   getSessionId()    { return sessionId; }
    public String getType()         { return type; }
    public String getStatus()       { return status; }
    public Long   getCustomerId()   { return customerId; }
    public String getCustomerName() { return customerName; }
    public Long   getStaffId()      { return staffId; }

    public void setSessionId(Long sessionId)       { this.sessionId = sessionId; }
    public void setType(String type)               { this.type = type; }
    public void setStatus(String status)           { this.status = status; }
    public void setCustomerId(Long customerId)     { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setStaffId(Long staffId)           { this.staffId = staffId; }
}