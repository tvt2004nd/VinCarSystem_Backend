package com.vin.VinSystem.Chat.Controller;

import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Repository.ChatMessageRepository;
import com.vin.VinSystem.Common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/messages")
public class ChatMessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ── Thu hồi (giữ record, ẩn nội dung) ──────────────────────────
    @PutMapping("/{messageId}/recall")
    public ApiResponse<Map<String, Object>> recall(@PathVariable Long messageId) {
        ChatMessage msg = chatMessageRepository.findById(messageId).orElse(null);
        if (msg == null) throw new RuntimeException("Message not found");

        if (msg.isRecalled()) {
            throw new IllegalArgumentException("Tin nhắn đã thu hồi trước đó");
        }

        msg.setRecalled(true);
        msg.setMessageText("[Tin nhắn đã thu hồi]");
        msg.setFileUrl(null);
        msg.setFileName(null);
        msg.setFileSize(null);
        msg.setFileMime(null);
        chatMessageRepository.save(msg);

        Long sessionId = msg.getSession().getSessionId();

        // Broadcast RECALL event → tất cả client tự cập nhật
        Map<String, Object> event = new HashMap<>();
        event.put("event",     "RECALL");
        event.put("messageId", messageId);
        event.put("sessionId", sessionId);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, event);

        Map<String, Object> result = new HashMap<>();
        result.put("success",   true);
        result.put("messageId", messageId);
        return ApiResponse.success(result, "Thu hồi thành công");
    }

    // ── Chỉnh sửa nội dung text ──────────────────────────────────────
    @PutMapping("/{messageId}/edit")
    public ApiResponse<Map<String, Object>> edit(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> body) {

        String newMessage = body.get("newMessage");
        if (newMessage == null || newMessage.isBlank()) {
            throw new IllegalArgumentException("Nội dung tin nhắn không được để trống");
        }

        ChatMessage msg = chatMessageRepository.findById(messageId).orElse(null);
        if (msg == null) throw new RuntimeException("Message not found");

        if (msg.isRecalled()) {
            throw new IllegalArgumentException("Không thể chỉnh sửa tin nhắn đã thu hồi");
        }

        // Chỉ cho phép edit TEXT
        if (msg.getMessageType() != ChatMessage.MessageType.TEXT) {
            throw new IllegalArgumentException("Chỉ có thể chỉnh sửa tin nhắn văn bản");
        }

        msg.setMessageText(newMessage.trim());
        msg.setEdited(true);
        chatMessageRepository.save(msg);

        Long sessionId = msg.getSession().getSessionId();

        // Broadcast EDIT event
        Map<String, Object> event = new HashMap<>();
        event.put("event",      "EDIT");
        event.put("messageId",  messageId);
        event.put("newMessage", newMessage.trim());
        event.put("sessionId",  sessionId);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, event);

        Map<String, Object> result = new HashMap<>();
        result.put("success",   true);
        result.put("messageId", messageId);
        return ApiResponse.success(result, "Chỉnh sửa thành công");
    }

    // ── Xóa hoàn toàn (chỉ admin) ────────────────────────────────────
    @DeleteMapping("/{messageId}")
    public ApiResponse<Map<String, Object>> delete(@PathVariable Long messageId) {
        ChatMessage msg = chatMessageRepository.findById(messageId).orElse(null);
        if (msg == null) throw new RuntimeException("Message not found");

        Long sessionId = msg.getSession().getSessionId();
        chatMessageRepository.deleteById(messageId);

        // Broadcast DELETE event
        Map<String, Object> event = new HashMap<>();
        event.put("event",     "DELETE");
        event.put("messageId", messageId);
        event.put("sessionId", sessionId);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, event);

        Map<String, Object> result = new HashMap<>();
        result.put("success",   true);
        result.put("messageId", messageId);
        return ApiResponse.success(result, "Xóa thành công");
    }
}