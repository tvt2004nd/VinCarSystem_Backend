package com.vin.VinSystem.Chat.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Entity.ChatMessage.MessageType;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatMessageRepository;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    public ChatSession getSession(Long sessionId) {
        return chatSessionRepository.findById(sessionId).orElseThrow();
    }

    // ── Lưu tin nhắn TEXT ────────────────────────────────────────────
    public ChatMessage saveMessage(
            Long sessionId,
            String senderType,
            Long senderId,
            String text) {

        ChatSession session = getSession(sessionId);

        ChatMessage msg = new ChatMessage();
        msg.setSession(session);
        msg.setSenderType(senderType);
        msg.setSenderId(senderId);
        msg.setMessageText(text);
        msg.setMessageType(MessageType.TEXT);

        return chatMessageRepository.save(msg);
    }

    // ── Lưu tin nhắn FILE hoặc IMAGE ────────────────────────────────
    public ChatMessage saveFileMessage(
            Long sessionId,
            String senderType,
            Long senderId,
            String senderName,
            String fileUrl,
            String fileName,
            Long fileSize,
            String fileMime) {

        ChatSession session = getSession(sessionId);

        MessageType type = (fileMime != null && fileMime.startsWith("image/"))
                           ? MessageType.IMAGE
                           : MessageType.FILE;

        ChatMessage msg = new ChatMessage();
        msg.setSession(session);
        msg.setSenderType(senderType);
        msg.setSenderId(senderId);
        msg.setSenderName(senderName);
        msg.setMessageType(type);
        msg.setMessageText(fileName); // fallback text = tên file
        msg.setFileUrl(fileUrl);
        msg.setFileName(fileName);
        msg.setFileSize(fileSize);
        msg.setFileMime(fileMime);

        return chatMessageRepository.save(msg);
    }

    // ── Lấy toàn bộ tin nhắn theo thời gian tăng dần ────────────────
    public List<ChatMessage> getMessages(Long sessionId) {
        return chatMessageRepository
                .findBySessionSessionIdOrderByCreatedAtAsc(sessionId);
    }
}