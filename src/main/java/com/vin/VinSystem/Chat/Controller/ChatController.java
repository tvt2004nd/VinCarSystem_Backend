package com.vin.VinSystem.Chat.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Chat.DTO.ChatMessageDTO;
import com.vin.VinSystem.Chat.DTO.ChatSessionDTO;
import com.vin.VinSystem.Chat.DTO.ChatSessionMapper;
import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;
import com.vin.VinSystem.Chat.Service.AIChatService;
import com.vin.VinSystem.Chat.Service.ChatService;
import com.vin.VinSystem.Chat.Service.OnlineStaffManager;
import com.vin.VinSystem.Chat.Service.StaffRoutingService;
import com.vin.VinSystem.Notification.Service.NotificationService;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private AIChatService aiChatService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChatSessionRepository sessionRepo;

    @Autowired
    private OnlineStaffManager onlineStaff;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StaffRoutingService staffRoutingService;

    /*
     =========================
     SEND MESSAGE
     =========================
     */

        /*
     =========================
     SEND MESSAGE (ĐÃ SỬA - HỖ TRỢ ẢNH + FILE)
     =========================
     */
    @MessageMapping("/chat/send")
    public void send(ChatMessageDTO message) {

        ChatSession session = sessionRepo.findById(message.getSessionId())
                .orElseThrow();

        log.info("Send: session={} type={} status={} staff={}",
                session.getSessionId(), session.getType(), session.getStatus(),
                session.getStaff() != null ? session.getStaff().getUserId() : "null");

        ChatMessage savedMessage;

        // ==================== XỬ LÝ FILE / ẢNH ====================
        boolean isFileMessage = "IMAGE".equals(message.getMessageType()) ||
                                "FILE".equals(message.getMessageType()) ||
                                message.getFileUrl() != null;

        if (isFileMessage) {
            savedMessage = chatService.saveFileMessage(
                    message.getSessionId(),
                    message.getSenderType(),
                    message.getSenderId(),
                    message.getSenderName(),           // ← thêm tên người gửi
                    message.getFileUrl(),
                    message.getFileName(),
                    message.getFileSize(),
                    message.getFileMime()
            );
        } 
        // ==================== TEXT THÔNG THƯỜNG ====================
        else {
            savedMessage = chatService.saveMessage(
                    message.getSessionId(),
                    message.getSenderType(),
                    message.getSenderId(),
                    message.getMessage()
            );
        }

        // Broadcast lại cho tất cả client (có ID và thời gian từ DB)
        ChatMessageDTO responseDto = new ChatMessageDTO(savedMessage);
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getSessionId(),
                responseDto
        );

        // BOT SESSION (chỉ reply khi là text, không reply khi gửi ảnh/file)
        if ("BOT".equals(session.getType()) && !isFileMessage) {
            CompletableFuture.runAsync(() -> {
                String reply = aiChatService.handleMessage(
                        message.getSessionId(),
                        message.getMessage()
                );

                ChatMessage savedBotMsg = chatService.saveMessage(message.getSessionId(), "BOT", null, reply);
                ChatMessageDTO botReply = new ChatMessageDTO(savedBotMsg);

                messagingTemplate.convertAndSend(
                        "/topic/chat/" + message.getSessionId(), botReply
                );
            });
            return;
        }

        // STAFF SESSION — assign nếu chưa có staff
        if ("STAFF".equals(session.getType()) && session.getStaff() == null) {
            Long staffId = staffRoutingService.pickStaff();
            log.info("Picked staff {} for session {}", staffId, session.getSessionId());

            if (staffId != null) {
                boolean assigned = staffRoutingService.tryAssignStaff(
                        session.getSessionId(), staffId
                );
                log.info("Assign result: {} for session {}", assigned, session.getSessionId());

                if (assigned) {
                    ChatSession updated = sessionRepo.findById(session.getSessionId())
                            .orElseThrow();
                    notifyAssignment(updated);
                }
            } else {
                log.warn("No staff online for session {}", session.getSessionId());
            }
        }
    }

    /*
     =========================
     TYPING & READ STATUS
     =========================
     */

    @MessageMapping("/chat/typing")
    public void handleTyping(Map<String, Object> payload) {
        Long sessionId = Long.valueOf(payload.get("sessionId").toString());
        String senderName = (String) payload.get("senderName");
        boolean isTyping = (boolean) payload.get("isTyping");

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("senderName", senderName);
        response.put("isTyping", isTyping);
        response.put("type", "TYPING");

        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, response);
    }

    @MessageMapping("/chat/read")
    public void handleRead(Map<String, Object> payload) {
        Long sessionId = Long.valueOf(payload.get("sessionId").toString());
        String senderType = (String) payload.get("senderType");

        chatService.markRead(sessionId, senderType);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("senderType", senderType);
        response.put("type", "READ");

        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, response);
    }

    @MessageMapping("/staff/online")
    public void staffOnline(Map<String, Object> payload) {
        Long staffId = Long.valueOf(payload.get("staffId").toString());
        onlineStaff.staffOnline(staffId);
        log.info("Staff {} online. Online list: {}", staffId, onlineStaff.getOnlineStaff());

        List<ChatSession> unassigned = sessionRepo.findUnassignedWaitingSessions();
        log.info("Unassigned waiting sessions: {}", unassigned.size());

        for (ChatSession session : unassigned) {
            boolean assigned = staffRoutingService.tryAssignStaff(
                    session.getSessionId(), staffId
            );
            if (assigned) {
                ChatSession updated = sessionRepo.findById(session.getSessionId())
                        .orElseThrow();
                notifyAssignment(updated);
                break;
            }
        }
    }

    /*
     =========================
     STAFF OFFLINE
     =========================
     */

    @MessageMapping("/staff/offline")
    public void staffOffline(Map<String, Object> payload) {
        Long staffId = Long.valueOf(payload.get("staffId").toString());
        onlineStaff.staffOffline(staffId);
        log.info("Staff {} offline.", staffId);
    }

    /*
     =========================
     HELPER: notify sau khi assign
     =========================
     */

    private void notifyAssignment(ChatSession session) {
        Long staffUserId = session.getStaff().getUserId();

        // Notify staff: có session mới
        ChatSessionDTO dto = ChatSessionMapper.toDTO(session);
        messagingTemplate.convertAndSend("/topic/staff/" + staffUserId, dto);

        notificationService.notifyStaff(
                staffUserId,
                "Khách mới cần tư vấn",
                "Có khách đang chat",
                String.valueOf(session.getSessionId())
        );

        // Notify customer: staff đã tham gia
        String staffName = session.getStaff().getUser().getName();
        ChatMessageDTO joinMsg = new ChatMessageDTO(
                session.getSessionId(), "SYSTEM", null,
                "Nhân viên " + staffName + " đã tham gia cuộc trò chuyện."
        );
        chatService.saveMessage(
                session.getSessionId(), "SYSTEM", null, joinMsg.getMessage()
        );
        messagingTemplate.convertAndSend(
                "/topic/chat/" + session.getSessionId(), joinMsg
        );
    }

    /*
     =========================
     CREATE STAFF SESSION (legacy)
     =========================
     */

    @PostMapping("/session/staff")
    public ChatSessionDTO createStaffSession(@RequestBody Map<String, Object> req) {
        Long customerId = Long.valueOf(req.get("customerId").toString());
        User customer = userRepo.findById(customerId).orElse(null);

        ChatSession session = new ChatSession();
        session.setCustomer(customer);
        session.setType("STAFF");
        session.setStatus("WAITING");
        sessionRepo.save(session);

        return ChatSessionMapper.toDTO(session);
    }
}