package com.vin.VinSystem.Config;  // hoặc .Security, tuỳ bạn tổ chức

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

@Component
public class StaffSessionAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private ChatSessionRepository sessionRepo;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String dest = accessor.getDestination();

            if (dest != null && dest.startsWith("/topic/chat/")) {
                Long sessionId = Long.parseLong(dest.replace("/topic/chat/", ""));
                Long staffId = getStaffIdFromHeader(accessor);

                if (staffId != null) {
                    ChatSession session = sessionRepo.findById(sessionId).orElse(null);
                    if (session == null
                            || session.getStaff() == null
                            || !session.getStaff().getUserId().equals(staffId)) { // ← getUserId()
                        throw new MessagingException("Unauthorized session access");
                    }
                }
            }
        }
        return message;
    }

    private Long getStaffIdFromHeader(StompHeaderAccessor accessor) {
        String staffIdHeader = accessor.getFirstNativeHeader("staffId");
        return staffIdHeader != null ? Long.valueOf(staffIdHeader) : null;
    }
}