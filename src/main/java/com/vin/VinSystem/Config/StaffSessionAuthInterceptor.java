package com.vin.VinSystem.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;
import com.vin.VinSystem.Security.JwtUtil;

@Component
public class StaffSessionAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private ChatSessionRepository sessionRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Kiểm tra TOKEN khi CONNECT hoặc SUBSCRIBE
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String token = extractToken(accessor);
            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                User user = userRepo.findByUsername(username).orElse(null);
                if (user != null) {
                    accessor.setUser(() -> username);
                    // Lưu userId vào session attributes để dùng sau
                    accessor.getSessionAttributes().put("userId", user.getUserId());
                    accessor.getSessionAttributes().put("username", username);
                }
            } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                // Nếu SUBSCRIBE mà không có token hợp lệ (và là private topic) thì chặn
                String dest = accessor.getDestination();
                if (dest != null && (dest.startsWith("/topic/chat/") || dest.startsWith("/topic/notifications/"))) {
                    throw new MessagingException("Unauthorized: Invalid or missing token");
                }
            }
        }

        // Kiểm tra quyền truy cập Session cụ thể khi SUBSCRIBE /topic/chat/{id}
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String dest = accessor.getDestination();
            if (dest != null && dest.startsWith("/topic/chat/")) {
                Long sessionId = Long.parseLong(dest.replace("/topic/chat/", ""));
                Long userId = (Long) accessor.getSessionAttributes().get("userId");

                if (userId == null) {
                    // Cho phép Anonymous chat (Bot) nếu session là BOT và guest_token khớp
                    // Ở đây tạm thời chặn nếu không có userId (bắt buộc login để chat staff)
                    ChatSession session = sessionRepo.findById(sessionId).orElse(null);
                    if (session != null && "STAFF".equals(session.getType())) {
                        throw new MessagingException("Unauthorized: Login required for staff chat");
                    }
                } else {
                    ChatSession session = sessionRepo.findById(sessionId).orElse(null);
                    if (session != null) {
                        boolean isCustomer = session.getCustomer() != null && session.getCustomer().getUserId().equals(userId);
                        boolean isStaff = session.getStaff() != null && session.getStaff().getUserId().equals(userId);
                        
                        if (!isCustomer && !isStaff) {
                            throw new MessagingException("Unauthorized: You are not part of this chat session");
                        }
                    }
                }
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}