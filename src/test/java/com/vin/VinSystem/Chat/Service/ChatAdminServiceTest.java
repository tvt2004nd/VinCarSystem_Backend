package com.vin.VinSystem.Chat.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Auth.Entity.Staff;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.StaffRepository;
import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatMessageRepository;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

class ChatAdminServiceTest {

    ChatSessionRepository sessionRepo = mock(ChatSessionRepository.class);
    ChatMessageRepository messageRepo = mock(ChatMessageRepository.class);
    StaffRepository       staffRepo   = mock(StaffRepository.class);
    ChatAdminService      service;

    @BeforeEach
    void setUp() {
        service = new ChatAdminService();
        ReflectionTestUtils.setField(service, "sessionRepo", sessionRepo);
        ReflectionTestUtils.setField(service, "messageRepo", messageRepo);
        ReflectionTestUtils.setField(service, "staffRepo",   staffRepo);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private ChatSession mockSession(Long id, String type, String status) {
        ChatSession s = new ChatSession();
        s.setSessionId(id);
        s.setType(type);
        s.setStatus(status);
        return s;
    }

    private Staff mockStaff(Long id, String name) {
        User u = new User();
        u.setUserId(id);
        u.setName(name);
        u.setUsername("staff" + id);
        Staff s = new Staff();
        s.setUserId(id);
        s.setUser(u);
        s.setPosition("Tư vấn bán hàng");
        return s;
    }

    private ChatMessage mockMessage(Long sessionId, String senderType, Long senderId, String text) {
        ChatMessage m = new ChatMessage();
        m.setMessageId(1L);
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        m.setSession(session);
        m.setSenderType(senderType);
        m.setSenderId(senderId);
        m.setMessageText(text);
        return m;
    }

    // ── getStats ──────────────────────────────────────────────────────────

    @Test
    void getStats_success() {
        List<Object[]> byType = new ArrayList<>();
        byType.add(new Object[]{"BOT",   6L});
        byType.add(new Object[]{"STAFF", 4L});

        List<Object[]> byStatus = new ArrayList<>();
        byStatus.add(new Object[]{"ACTIVE", 3L});
        byStatus.add(new Object[]{"CLOSED", 7L});

        List<Object[]> bySender = new ArrayList<>();
        bySender.add(new Object[]{"CUSTOMER", 30L});
        bySender.add(new Object[]{"STAFF",    20L});

        List<Object[]> bySessionType = new ArrayList<>();
        bySessionType.add(new Object[]{"BOT",   25L});
        bySessionType.add(new Object[]{"STAFF", 25L});

        when(sessionRepo.count()).thenReturn(10L);
        when(sessionRepo.countToday()).thenReturn(2L);
        when(messageRepo.count()).thenReturn(50L);
        when(messageRepo.countToday()).thenReturn(5L);
        when(sessionRepo.countByStatus("ACTIVE")).thenReturn(3L);
        when(sessionRepo.countByStatus("WAITING")).thenReturn(1L);
        when(sessionRepo.countByType()).thenReturn(byType);
        when(sessionRepo.countByStatusGroup()).thenReturn(byStatus);
        when(messageRepo.countBySenderType()).thenReturn(bySender);
        when(messageRepo.countBySessionType()).thenReturn(bySessionType);

        Map<String, Object> stats = service.getStats();

        assertThat(stats.get("totalSessions")).isEqualTo(10L);
        assertThat(stats.get("sessionsToday")).isEqualTo(2L);
        assertThat(stats.get("totalMessages")).isEqualTo(50L);
        assertThat(stats.get("activeSessions")).isEqualTo(3L);
        assertThat(stats.get("waitingSessions")).isEqualTo(1L);

        @SuppressWarnings("unchecked")
        Map<String, Long> typeMap = (Map<String, Long>) stats.get("sessionByType");
        assertThat(typeMap).containsEntry("BOT", 6L).containsEntry("STAFF", 4L);
    }

    @Test
    void getStats_nullToday_defaultsToZero() {
        when(sessionRepo.count()).thenReturn(0L);
        when(sessionRepo.countToday()).thenReturn(null);
        when(messageRepo.count()).thenReturn(0L);
        when(messageRepo.countToday()).thenReturn(null);
        when(sessionRepo.countByStatus(any())).thenReturn(0L);
        when(sessionRepo.countByType()).thenReturn(new ArrayList<>());
        when(sessionRepo.countByStatusGroup()).thenReturn(new ArrayList<>());
        when(messageRepo.countBySenderType()).thenReturn(new ArrayList<>());
        when(messageRepo.countBySessionType()).thenReturn(new ArrayList<>());

        Map<String, Object> stats = service.getStats();

        assertThat(stats.get("sessionsToday")).isEqualTo(0L);
        assertThat(stats.get("messagesToday")).isEqualTo(0L);
    }

    // ── getDailyStats ─────────────────────────────────────────────────────

    @Test
    void getDailyStats_success() {
        List<Object[]> msgByDay = new ArrayList<>();
        msgByDay.add(new Object[]{"2026-04-01", 10L});
        msgByDay.add(new Object[]{"2026-04-02", 15L});

        List<Object[]> sessByDay = new ArrayList<>();
        sessByDay.add(new Object[]{"2026-04-01", 3L});

        when(messageRepo.countByDayLast7Days()).thenReturn(msgByDay);
        when(sessionRepo.countSessionsByDayLast7Days()).thenReturn(sessByDay);

        Map<String, Object> daily = service.getDailyStats();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> msgs = (List<Map<String, Object>>) daily.get("messagesByDay");
        assertThat(msgs).hasSize(2);
        assertThat(msgs.get(0).get("date")).isEqualTo("2026-04-01");
        assertThat(msgs.get(0).get("count")).isEqualTo(10L);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sess = (List<Map<String, Object>>) daily.get("sessionsByDay");
        assertThat(sess).hasSize(1);
    }

    // ── getStaffStats ─────────────────────────────────────────────────────

    @Test
    void getStaffStats_sortedByScore() {
        List<Object[]> msgByStaff = new ArrayList<>();
        msgByStaff.add(new Object[]{7L,  20L});
        msgByStaff.add(new Object[]{11L,  5L});

        List<Object[]> sessByStaff = new ArrayList<>();
        sessByStaff.add(new Object[]{7L,  10L});
        sessByStaff.add(new Object[]{11L,  2L});

        when(messageRepo.countMessagesByStaff()).thenReturn(msgByStaff);
        when(messageRepo.countSessionsByStaff()).thenReturn(sessByStaff);
        when(staffRepo.findById(7L)).thenReturn(Optional.of(mockStaff(7L,  "Thanh Tuấn")));
        when(staffRepo.findById(11L)).thenReturn(Optional.of(mockStaff(11L, "Hoàng Quân")));

        List<Map<String, Object>> result = service.getStaffStats();

        assertThat(result).hasSize(2);
        // staff 7: score = 10*0.6 + 20*0.4 = 14 → top
        assertThat(result.get(0).get("staffName")).isEqualTo("Thanh Tuấn");
        assertThat(result.get(0).get("sessions")).isEqualTo(10L);
        assertThat(result.get(0).get("messages")).isEqualTo(20L);
    }

    @Test
    void getStaffStats_nullStaffId_skipped() {
        List<Object[]> msgByStaff = new ArrayList<>();
        msgByStaff.add(new Object[]{null, 10L});
        msgByStaff.add(new Object[]{7L,    5L});

        when(messageRepo.countMessagesByStaff()).thenReturn(msgByStaff);
        when(messageRepo.countSessionsByStaff()).thenReturn(new ArrayList<>());
        when(staffRepo.findById(7L)).thenReturn(Optional.of(mockStaff(7L, "Tuấn")));

        List<Map<String, Object>> result = service.getStaffStats();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("staffId")).isEqualTo(7L);
    }

    // ── getAllSessions ────────────────────────────────────────────────────

    @Test
    void getAllSessions_withCustomerAndStaff() {
        User customer = new User();
        customer.setName("Trần Văn Tiền");

        Staff staff = mockStaff(7L, "Thanh Tuấn");

        ChatSession session = mockSession(1L, "STAFF", "ACTIVE");
        session.setCustomer(customer);
        session.setStaff(staff);

        List<Object[]> msgCount = new ArrayList<>();
        msgCount.add(new Object[]{1L, 5L});

        when(sessionRepo.findAll()).thenReturn(List.of(session));
        when(messageRepo.countMessagesGroupBySession()).thenReturn(msgCount);

        List<Map<String, Object>> result = service.getAllSessions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("customerName")).isEqualTo("Trần Văn Tiền");
        assertThat(result.get(0).get("staffName")).isEqualTo("Thanh Tuấn");
        assertThat(result.get(0).get("messageCount")).isEqualTo(5L);
    }

    @Test
    void getAllSessions_anonymousCustomer() {
        ChatSession session = mockSession(2L, "BOT", "ACTIVE");
        session.setCustomer(null);

        when(sessionRepo.findAll()).thenReturn(List.of(session));
        when(messageRepo.countMessagesGroupBySession()).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = service.getAllSessions();

        assertThat(result.get(0).get("customerName")).isEqualTo("Khách ẩn danh");
        assertThat(result.get(0).get("messageCount")).isEqualTo(0L);
    }

    // ── getMessages ───────────────────────────────────────────────────────

    @Test
    void getMessages_filtersBotMessages() {
        List<ChatMessage> messages = List.of(
            mockMessage(1L, "CUSTOMER", 3L,   "xin chào"),
            mockMessage(1L, "BOT",      null, "chào bạn"),
            mockMessage(1L, "STAFF",    7L,   "tôi hỗ trợ")
        );
        when(messageRepo.findBySessionSessionIdOrderByCreatedAtAsc(1L))
            .thenReturn(messages);
        when(staffRepo.findById(7L)).thenReturn(Optional.of(mockStaff(7L, "Thanh Tuấn")));

        List<Map<String, Object>> result = service.getMessages(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("senderType")).isEqualTo("CUSTOMER");
        assertThat(result.get(1).get("senderName")).isEqualTo("Thanh Tuấn");
    }

    // ── joinSession ───────────────────────────────────────────────────────

    @Test
    void joinSession_success() {
        ChatSession session = mockSession(1L, "STAFF", "WAITING");
        Staff staff = mockStaff(7L, "Thanh Tuấn");

        when(sessionRepo.findById(1L)).thenReturn(Optional.of(session));
        when(staffRepo.findById(7L)).thenReturn(Optional.of(staff));

        Map<String, Object> result = service.joinSession(1L, 7L);

        assertThat(result.get("success")).isEqualTo(true);
        assertThat(session.getStatus()).isEqualTo("ACTIVE");
        assertThat(session.getStaff()).isEqualTo(staff);
        verify(sessionRepo).save(session);
    }

    @Test
    void joinSession_sessionNotFound_throws() {
        when(sessionRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.joinSession(99L, 7L))
            .isInstanceOf(NoSuchElementException.class);
    }

    // ── transferSession ───────────────────────────────────────────────────

    @Test
    void transferSession_success() {
        ChatSession session = mockSession(1L, "STAFF", "ACTIVE");
        Staff newStaff = mockStaff(11L, "Hoàng Quân");

        when(sessionRepo.findById(1L)).thenReturn(Optional.of(session));
        when(staffRepo.findById(11L)).thenReturn(Optional.of(newStaff));

        Map<String, Object> result = service.transferSession(1L, 11L);

        assertThat(result.get("success")).isEqualTo(true);
        assertThat(session.getStaff()).isEqualTo(newStaff);
        verify(sessionRepo).save(session);
    }

    @Test
    void transferSession_staffNotFound_throws() {
        when(sessionRepo.findById(1L)).thenReturn(Optional.of(mockSession(1L, "STAFF", "ACTIVE")));
        when(staffRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferSession(1L, 99L))
            .isInstanceOf(NoSuchElementException.class);
    }

    // ── closeSession ──────────────────────────────────────────────────────

    @Test
    void closeSession_success() {
        ChatSession session = mockSession(1L, "STAFF", "ACTIVE");
        when(sessionRepo.findById(1L)).thenReturn(Optional.of(session));

        Map<String, Object> result = service.closeSession(1L);

        assertThat(result.get("success")).isEqualTo(true);
        assertThat(session.getStatus()).isEqualTo("CLOSED");
        verify(sessionRepo).save(session);
    }

    @Test
    void closeSession_notFound_throws() {
        when(sessionRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.closeSession(99L))
            .isInstanceOf(NoSuchElementException.class);
    }
}