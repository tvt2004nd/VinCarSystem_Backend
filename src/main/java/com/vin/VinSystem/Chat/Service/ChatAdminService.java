package com.vin.VinSystem.Chat.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatMessageRepository;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

@Service
public class ChatAdminService {

    @Autowired
    private ChatSessionRepository sessionRepo;

    @Autowired
    private ChatMessageRepository messageRepo;

    @Autowired
    private StaffRepository staffRepo;

    /*
     ========================
     STATS
     ========================
     */
    public Map<String, Object> getStats() {

        long totalSessions = sessionRepo.count();
        long sessionsToday = Optional.ofNullable(sessionRepo.countToday()).orElse(0L);
        long totalMessages = messageRepo.count();
        long messagesToday = Optional.ofNullable(messageRepo.countToday()).orElse(0L);

        Map<String, Long> sessionByType = new LinkedHashMap<>();
        for (Object[] row : sessionRepo.countByType()) {
            sessionByType.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }

        Map<String, Long> sessionByStatus = new LinkedHashMap<>();
        for (Object[] row : sessionRepo.countByStatusGroup()) {
            sessionByStatus.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }

        Map<String, Long> messagesBySender = new LinkedHashMap<>();
        for (Object[] row : messageRepo.countBySenderType()) {
            messagesBySender.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }

        Map<String, Long> messagesBySessionType = new LinkedHashMap<>();
        for (Object[] row : messageRepo.countBySessionType()) {
            messagesBySessionType.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }

        return Map.of(
                "totalSessions", totalSessions,
                "sessionsToday", sessionsToday,
                "totalMessages", totalMessages,
                "messagesToday", messagesToday,
                "activeSessions", sessionRepo.countByStatus("ACTIVE"),
                "waitingSessions", sessionRepo.countByStatus("WAITING"),
                "sessionByType", sessionByType,
                "sessionByStatus", sessionByStatus,
                "messagesBySender", messagesBySender,
                "messagesBySessionType", messagesBySessionType);
    }

    /*
     ========================
     DAILY STATS
     ========================
     */
    public Map<String, Object> getDailyStats() {

        List<Map<String, Object>> messages = new ArrayList<>();
        for (Object[] row : messageRepo.countByDayLast7Days()) {
            messages.add(Map.of(
                    "date", String.valueOf(row[0]),
                    "count", ((Number) row[1]).longValue()));
        }

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Object[] row : sessionRepo.countSessionsByDayLast7Days()) {
            sessions.add(Map.of(
                    "date", String.valueOf(row[0]),
                    "count", ((Number) row[1]).longValue()));
        }

        return Map.of(
                "messagesByDay", messages,
                "sessionsByDay", sessions);
    }

    /*
     ========================
     STAFF STATS
     ========================
     */
    public List<Map<String, Object>> getStaffStats() {

        Map<Long, Long> msgMap = new LinkedHashMap<>();
        for (Object[] row : messageRepo.countMessagesByStaff()) {
            if (row[0] == null) continue;
            msgMap.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }

        Map<Long, Long> sessMap = new LinkedHashMap<>();
        for (Object[] row : messageRepo.countSessionsByStaff()) {
            if (row[0] == null) continue;
            sessMap.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }

        Set<Long> ids = new LinkedHashSet<>();
        ids.addAll(msgMap.keySet());
        ids.addAll(sessMap.keySet());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Long id : ids) {

            String name = "Staff #" + id;
            String position = "";

            Staff staff = staffRepo.findById(id).orElse(null);

            if (staff != null && staff.getUser() != null) {
                name = staff.getUser().getName() != null
                        ? staff.getUser().getName()
                        : staff.getUser().getUsername();

                position = staff.getPosition() != null
                        ? staff.getPosition()
                        : "";
            }

            long messages = msgMap.getOrDefault(id, 0L);
            long sessions = sessMap.getOrDefault(id, 0L);

            double score = sessions * 0.6 + messages * 0.4;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("staffId", id);
            m.put("staffName", name);
            m.put("position", position);
            m.put("messages", messages);
            m.put("sessions", sessions);
            m.put("score", Math.round(score * 10.0) / 10.0);

            result.add(m);
        }

        result.sort((a, b) -> Double.compare(
                ((Number) b.get("score")).doubleValue(),
                ((Number) a.get("score")).doubleValue()));

        return result;
    }

    /*
     ========================
     ALL SESSIONS
     ========================
     */
    public List<Map<String, Object>> getAllSessions() {

        List<Map<String, Object>> result = new ArrayList<>();

        Map<Long, Long> msgCountMap = new HashMap<>();

        for (Object[] r : messageRepo.countMessagesGroupBySession()) {
            msgCountMap.put(
                    ((Number) r[0]).longValue(),
                    ((Number) r[1]).longValue());
        }

        sessionRepo.findAll().forEach(session -> {

            Map<String, Object> m = new LinkedHashMap<>();

            m.put("sessionId", session.getSessionId());
            m.put("type", session.getType());
            m.put("status", session.getStatus());

            if (session.getCustomer() != null) {
                m.put("customerName",
                        session.getCustomer().getName() != null
                                ? session.getCustomer().getName()
                                : session.getCustomer().getUsername());
            } else {
                m.put("customerName", "Khách ẩn danh");
            }

            if (session.getStaff() != null) {

                String sname = session.getStaff().getUser() != null
                        ? session.getStaff().getUser().getName()
                        : "Staff #" + session.getStaff().getUserId();

                m.put("staffName", sname);

            } else {
                m.put("staffName", "—");
            }

            long msgCount = msgCountMap.getOrDefault(session.getSessionId(), 0L);

            m.put("messageCount", msgCount);

            result.add(m);
        });

        return result;
    }
  public List<Map<String,Object>> getMessages(Long sessionId) {
    List<Map<String,Object>> result = new ArrayList<>();

    messageRepo
        .findBySessionSessionIdOrderByCreatedAtAsc(sessionId)
        .stream()
        .filter(m -> !"BOT".equals(m.getSenderType()))
        .forEach(m -> {
            Map<String,Object> msg = new LinkedHashMap<>();

            msg.put("messageId", m.getMessageId());
            msg.put("senderType", m.getSenderType());
            msg.put("senderId", m.getSenderId());
            msg.put("time", m.getCreatedAt());
            msg.put("recalled", m.isRecalled());
            msg.put("edited", m.isEdited());

            if ("STAFF".equals(m.getSenderType())) {
                Staff staff = staffRepo.findById(m.getSenderId()).orElse(null);
                if (staff != null && staff.getUser() != null) {
                    msg.put("senderName", staff.getUser().getName());
                }
            }

            if (m.isRecalled()) {
                msg.put("messageType", "TEXT");
                msg.put("message", "[Tin nhắn đã thu hồi]");
            } else {
                msg.put("messageType", m.getMessageType() != null ? m.getMessageType().name() : "TEXT");
                msg.put("message", m.getMessageText());
                msg.put("fileUrl", m.getFileUrl());
                msg.put("fileName", m.getFileName());
                msg.put("fileSize", m.getFileSize());
                msg.put("fileMime", m.getFileMime());
            }

            result.add(msg);
        });

    return result;
}
public Map<String,Object> joinSession(Long sessionId, Long staffId){

    ChatSession session =
        sessionRepo.findById(sessionId).orElseThrow();

    Staff staff =
        staffRepo.findById(staffId).orElseThrow();

    session.setStaff(staff);
    session.setStatus("ACTIVE");

    sessionRepo.save(session);

    return Map.of(
        "success", true,
        "message", "Staff joined session"
    );
}
public Map<String,Object> transferSession(Long sessionId, Long staffId){

    ChatSession session =
        sessionRepo.findById(sessionId).orElseThrow();

    Staff newStaff =
        staffRepo.findById(staffId).orElseThrow();

    session.setStaff(newStaff);

    sessionRepo.save(session);

    return Map.of(
        "success", true,
        "message", "Session transferred"
    );
}
public Map<String,Object> closeSession(Long sessionId){

    ChatSession session =
        sessionRepo.findById(sessionId).orElseThrow();

    session.setStatus("CLOSED");

    sessionRepo.save(session);

    return Map.of(
        "success", true,
        "message", "Session closed"
    );
}
}