package com.vin.VinSystem.Chat.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_chat_msg_session", columnList = "session_id"),
    @Index(name = "idx_chat_msg_created", columnList = "created_at")
})
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(name = "sender_type", length = 20)
    private String senderType; // CUSTOMER | STAFF | BOT | SYSTEM

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 20)
    private MessageType messageType = MessageType.TEXT;

    public enum MessageType { TEXT, IMAGE, FILE }

    @Column(name = "message_text", columnDefinition = "TEXT")
    private String messageText;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_mime", length = 100)
    private String fileMime;

    // ✅ Dùng Boolean (wrapper) thay vì boolean (primitive)
    // → tránh lỗi "Null value assigned to primitive type" với record cũ trong DB
    @Column(name = "recalled", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean recalled = false;

    // true = đã chỉnh sửa sau khi gửi
    @Column(name = "edited", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean edited = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    public Long getMessageId()                { return messageId; }
    public void setMessageId(Long v)          { this.messageId = v; }
    public ChatSession getSession()           { return session; }
    public void setSession(ChatSession v)     { this.session = v; }
    public String getSenderType()             { return senderType; }
    public void setSenderType(String v)       { this.senderType = v; }
    public Long getSenderId()                 { return senderId; }
    public void setSenderId(Long v)           { this.senderId = v; }
    public String getSenderName()             { return senderName; }
    public void setSenderName(String v)       { this.senderName = v; }
    public MessageType getMessageType()       { return messageType; }
    public void setMessageType(MessageType v) { this.messageType = v; }
    public String getMessageText()            { return messageText; }
    public void setMessageText(String v)      { this.messageText = v; }
    public String getFileUrl()                { return fileUrl; }
    public void setFileUrl(String v)          { this.fileUrl = v; }
    public String getFileName()               { return fileName; }
    public void setFileName(String v)         { this.fileName = v; }
    public Long getFileSize()                 { return fileSize; }
    public void setFileSize(Long v)           { this.fileSize = v; }
    public String getFileMime()               { return fileMime; }
    public void setFileMime(String v)         { this.fileMime = v; }

    // ✅ Boolean getter — trả về false nếu null (an toàn với record cũ)
    public boolean isRecalled()               { return Boolean.TRUE.equals(recalled); }
    public void setRecalled(Boolean v)        { this.recalled = v; }
    public boolean isEdited()                 { return Boolean.TRUE.equals(edited); }
    public void setEdited(Boolean v)          { this.edited = v; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}