package com.vin.VinSystem.Chat.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

@Service
public class ChatSessionService {

    @Autowired
    private ChatSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    public ChatSession getOrCreateSession(Long customerId, String type) {

        // Anonymous user (chưa đăng nhập) + BOT → KHÔNG lưu DB
        // Frontend tự quản lý session bằng sessionId tạm thời (xem Chat.jsx)
        // Chỉ xử lý ở đây khi customerId != null
        if (customerId == null) {

            // Trả về null → ChatSessionController sẽ trả 400 hoặc
            // frontend không gọi endpoint này cho anonymous BOT
            // (xem Chat.jsx: anonymous BOT không gọi POST /chat)
            return null;
        }

        // STAFF session luôn cần DB (cần assign staff, lưu lịch sử)
        if ("STAFF".equals(type)) {

            // Tìm session STAFF còn đang WAITING hoặc ACTIVE
            ChatSession existing =
                    sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(
                            customerId, type, "CLOSED"
                    );

            if (existing != null) return existing;

            User user = userRepository.findById(customerId).orElse(null);

            ChatSession newSession = new ChatSession();
            newSession.setCustomer(user);
            newSession.setType("STAFF");
            newSession.setStatus("WAITING");

            return sessionRepository.save(newSession);
        }

        // BOT session với user đã đăng nhập → lưu DB như bình thường
        ChatSession existing =
                sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(
                        customerId, type, "CLOSED"
                );

        if (existing != null) return existing;

        User user = userRepository.findById(customerId).orElse(null);

        ChatSession newSession = new ChatSession();
        newSession.setCustomer(user);
        newSession.setType("BOT");
        newSession.setStatus("ACTIVE");

        return sessionRepository.save(newSession);
    }
    public List<ChatSession> getSessionsByStaff(Long staffId) {
    return sessionRepository.findSessionsByStaffId(staffId);
}
}