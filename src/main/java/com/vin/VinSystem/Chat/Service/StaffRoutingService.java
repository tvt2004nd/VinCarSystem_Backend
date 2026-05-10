package com.vin.VinSystem.Chat.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

@Service
public class StaffRoutingService {

    @Autowired
    private OnlineStaffManager onlineStaff;

    @Autowired
    private ChatSessionRepository sessionRepo;

    @Autowired
    private StaffRepository staffRepo;

    private final AtomicInteger counter = new AtomicInteger(0);

    public Long pickStaff() {
        List<Long> online = new ArrayList<>(onlineStaff.getOnlineStaff());
        if (online.isEmpty()) return null;
        return online.get(counter.getAndIncrement() % online.size());
    }

    /**
     * Atomic assign dùng PESSIMISTIC_WRITE lock.
     * Trả về true nếu assign thành công.
     * Trả về false nếu session đã có staff rồi (race condition).
     */
    @Transactional
    public boolean tryAssignStaff(Long sessionId, Long staffId) {
        ChatSession session = sessionRepo.findByIdForUpdate(sessionId)
                .orElseThrow();

        // Double-check sau khi lock
        if (session.getStaff() != null) {
            return false;
        }

        staffRepo.findById(staffId).ifPresent(staff -> {
            session.setStaff(staff);
            session.setStatus("ACTIVE");
        });

        if (session.getStaff() == null) {
            return false; // staffId không tồn tại trong DB
        }

        sessionRepo.save(session);
        return true;
    }
}