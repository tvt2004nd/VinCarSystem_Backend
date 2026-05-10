package com.vin.VinSystem.Chat.DTO;

import com.vin.VinSystem.Chat.Entity.ChatSession;

public class ChatSessionMapper {

    public static ChatSessionDTO toDTO(ChatSession session) {

        Long   customerId   = null;
        String customerName = null;
        Long   staffId      = null;

        if (session.getCustomer() != null) {
            customerId   = session.getCustomer().getUserId();
            // FIX: lấy tên từ User.name, fallback về username nếu name null
            customerName = session.getCustomer().getName() != null
                    ? session.getCustomer().getName()
                    : session.getCustomer().getUsername();
        }

        if (session.getStaff() != null) {
            staffId = session.getStaff().getUserId();
        }

        return new ChatSessionDTO(
                session.getSessionId(),
                session.getType(),
                session.getStatus(),
                customerId,
                customerName,
                staffId
        );
    }
}