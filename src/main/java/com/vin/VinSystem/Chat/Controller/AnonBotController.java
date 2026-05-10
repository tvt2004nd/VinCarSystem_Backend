package com.vin.VinSystem.Chat.Controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Chat.Service.AIChatService;
import com.vin.VinSystem.Chat.Service.ConversationHistoryService;

/**
 * Endpoint chat BOT cho user CHƯA đăng nhập.
 * Không lưu DB, chỉ dùng ConversationHistoryService (in-memory).
 * sessionId do frontend tự sinh (UUID), truyền lên mỗi request.
 */
@RestController
@RequestMapping("/api/chat/anon")
public class AnonBotController {

    @Autowired
    private AIChatService aiChatService;

    @Autowired
    private ConversationHistoryService historyService;

    /**
     * Tạo sessionId mới cho anonymous user.
     * Frontend gọi 1 lần khi mở chat, lưu vào sessionStorage.
     */
    @PostMapping("/session")
    public Map<String, String> createSession() {
        String sessionId = "anon-" + UUID.randomUUID();
        // Không lưu gì vào DB, chỉ trả sessionId
        return Map.of("sessionId", sessionId);
    }

    /**
     * Gửi tin nhắn và nhận reply từ AI.
     * sessionId là string (anon-UUID), dùng hashCode để map vào historyService.
     */
    @PostMapping("/message")
    public Map<String, String> sendMessage(@RequestBody Map<String, String> req) {

        String sessionId = req.get("sessionId");
        String message   = req.get("message");

        if (sessionId == null || message == null) {
            return Map.of("reply", "Yêu cầu không hợp lệ.");
        }

        // Dùng hashCode của sessionId string làm Long key cho historyService
        Long historyKey = (long) sessionId.hashCode();

        String reply = aiChatService.handleMessage(historyKey, message);

        return Map.of("reply", reply);
    }

    /**
     * Xóa history khi user đóng chat hoặc refresh.
     * Frontend gọi khi unmount hoặc trước khi tạo session mới.
     */
    @DeleteMapping("/session/{sessionId}/history")
    public Map<String, String> clearHistory(@PathVariable String sessionId) {

        Long historyKey = (long) sessionId.hashCode();
        historyService.clearHistory(historyKey);

        return Map.of("status", "cleared");
    }
}