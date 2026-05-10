package com.vin.VinSystem.Chat.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Chat.DTO.ChatMessageDTO;
import com.vin.VinSystem.Chat.DTO.ChatSessionDTO;
import com.vin.VinSystem.Chat.DTO.ChatSessionMapper;
import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;
import com.vin.VinSystem.Chat.Service.ChatService;
import com.vin.VinSystem.Chat.Service.ChatSessionService;
import com.vin.VinSystem.Chat.Service.ConversationHistoryService;

@RestController
@RequestMapping("/api/chat")
public class ChatSessionController {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ConversationHistoryService historyService;

    @Autowired
    private ChatSessionService chatSessionService;

    /*
     ================================
     CREATE SESSION
     ================================
     */

    @PostMapping
    public ChatSessionDTO createSession(
            @RequestParam String type,
            @RequestBody SessionRequest request
    ) {

        ChatSession session =
                chatSessionService.getOrCreateSession(
                        request.getCustomerId(),
                        type
                );

        return ChatSessionMapper.toDTO(session);
    }
@GetMapping("/staff/sessions")
public List<ChatSessionDTO> staffSessions(@RequestParam Long staffId) {
    return chatSessionService.getSessionsByStaff(staffId)
            .stream()
            .map(ChatSessionMapper::toDTO)
            .toList();
}

    public static class SessionRequest {

        private Long customerId;

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
    }

    /*
     ================================
     GET CHAT HISTORY
     ================================
     */

    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Long sessionId
    ) {

        List<ChatMessageDTO> list =
                chatService.getMessages(sessionId)
                        .stream()
                        .map(ChatMessageDTO::new)
                        .toList();

        return ResponseEntity.ok(list);
    }

    /*
     ================================
     CLEAR HISTORY
     ================================
     */

    @DeleteMapping("/session/{sessionId}/history")
    public ResponseEntity<Void> clearHistory(@PathVariable Long sessionId) {

        historyService.clearHistory(sessionId);

        return ResponseEntity.noContent().build();
    }

    /*
     ================================
     RESTORE AI HISTORY
     ================================
     */

    @PostMapping("/session/{sessionId}/restore")
    public ResponseEntity<Void> restoreHistory(
            @PathVariable Long sessionId
    ) {

        List<ChatMessage> messages =
                chatService.getMessages(sessionId);

        for (ChatMessage msg : messages) {

            String role = switch (msg.getSenderType()) {

                case "CUSTOMER" -> "user";
                case "BOT" -> "assistant";
                default -> "system";
            };

            historyService.addMessage(
                    sessionId,
                    role,
                    msg.getMessageText()
            );
        }

        return ResponseEntity.noContent().build();
    }

    /*
     ================================
     STAFF SESSION LIST
     ================================
     */


}