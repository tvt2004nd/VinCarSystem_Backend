package com.vin.VinSystem.Chat.DTO;

import com.vin.VinSystem.Chat.Entity.ChatMessage;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    private Long          messageId;
    private Long          sessionId;
    private String        senderType;
    private Long          senderId;
    private String        senderName;
    private String        messageType;
    private String        message;
    private String        fileUrl;
    private String        fileName;
    private Long          fileSize;
    private String        fileMime;
    private boolean       recalled;
    private boolean       edited;
    private String        status;
    private LocalDateTime time;

    public ChatMessageDTO() {}

    public ChatMessageDTO(ChatMessage m) {
        this.messageId   = m.getMessageId();
        this.sessionId   = m.getSession().getSessionId();
        this.senderType  = m.getSenderType();
        this.senderId    = m.getSenderId();
        this.senderName  = m.getSenderName();
        this.recalled    = m.isRecalled();
        this.edited      = m.isEdited();
        this.status      = m.getStatus() != null ? m.getStatus().name() : "SENT";
        this.time        = m.getCreatedAt();

        if (m.isRecalled()) {
            // Ẩn nội dung khi đã thu hồi
            this.messageType = "TEXT";
            this.message     = "[Tin nhắn đã thu hồi]";
        } else {
            this.messageType = m.getMessageType() != null ? m.getMessageType().name() : "TEXT";
            this.message     = m.getMessageText();
            this.fileUrl     = m.getFileUrl();
            this.fileName    = m.getFileName();
            this.fileSize    = m.getFileSize();
            this.fileMime    = m.getFileMime();
        }
    }

    /** Constructor nhanh cho WebSocket text */
    public ChatMessageDTO(Long sessionId, String senderType, Long senderId, String message) {
        this.sessionId   = sessionId;
        this.senderType  = senderType;
        this.senderId    = senderId;
        this.message     = message;
        this.messageType = "TEXT";
    }

    public Long          getMessageId()           { return messageId; }
    public void          setMessageId(Long v)      { this.messageId = v; }
    public Long          getSessionId()            { return sessionId; }
    public void          setSessionId(Long v)      { this.sessionId = v; }
    public String        getSenderType()           { return senderType; }
    public void          setSenderType(String v)   { this.senderType = v; }
    public Long          getSenderId()             { return senderId; }
    public void          setSenderId(Long v)       { this.senderId = v; }
    public String        getSenderName()           { return senderName; }
    public void          setSenderName(String v)   { this.senderName = v; }
    public String        getMessageType()          { return messageType; }
    public void          setMessageType(String v)  { this.messageType = v; }
    public String        getMessage()              { return message; }
    public void          setMessage(String v)      { this.message = v; }
    public String        getFileUrl()              { return fileUrl; }
    public void          setFileUrl(String v)      { this.fileUrl = v; }
    public String        getFileName()             { return fileName; }
    public void          setFileName(String v)     { this.fileName = v; }
    public Long          getFileSize()             { return fileSize; }
    public void          setFileSize(Long v)       { this.fileSize = v; }
    public String        getFileMime()             { return fileMime; }
    public void          setFileMime(String v)     { this.fileMime = v; }
    public boolean       isRecalled()              { return recalled; }
    public void          setRecalled(boolean v)    { this.recalled = v; }
    public boolean       isEdited()                { return edited; }
    public void          setEdited(boolean v)      { this.edited = v; }
    public String        getStatus()              { return status; }
    public void          setStatus(String v)      { this.status = v; }
    public LocalDateTime getTime()                 { return time; }
    public void          setTime(LocalDateTime v)  { this.time = v; }
}