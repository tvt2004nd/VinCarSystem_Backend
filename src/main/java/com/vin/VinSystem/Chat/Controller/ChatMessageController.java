package com.vin.VinSystem.Chat.Controller;

import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> recall(@PathVariable Long messageId) {
        ChatMessage msg = chatMessageRepository.findById(messageId).orElse(null);
        if (msg == null) return ResponseEntity.notFound().build();

        if (msg.isRecalled()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Tin nhắn đã thu hồi trước đó");
            return ResponseEntity.badRequest().body(err);
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
        return ResponseEntity.ok(result);
    }

    // ── Chỉnh sửa nội dung text ──────────────────────────────────────
    @PutMapping("/{messageId}/edit")
    public ResponseEntity<Map<String, Object>> edit(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> body) {

        String newMessage = body.get("newMessage");
        if (newMessage == null || newMessage.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Nội dung tin nhắn không được để trống");
            return ResponseEntity.badRequest().body(err);
        }

        ChatMessage msg = chatMessageRepository.findById(messageId).orElse(null);
        if (msg == null) return ResponseEntity.notFound().build();

        if (msg.isRecalled()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Không thể chỉnh sửa tin nhắn đã thu hồi");
            return ResponseEntity.badRequest().body(err);
        }

        // Chỉ cho phép edit TEXT
        if (msg.getMessageType() != ChatMessage.MessageType.TEXT) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Chỉ có thể chỉnh sửa tin nhắn văn bản");
            return ResponseEntity.badRequest().body(err);
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
        return ResponseEntity.ok(result);
    }

    // ── Xóa hoàn toàn (chỉ admin) ────────────────────────────────────
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long messageId) {
        ChatMessage msg = chatMessageRepository.findById(messageId).orElse(null);
        if (msg == null) return ResponseEntity.notFound().build();

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
        return ResponseEntity.ok(result);
    }
}