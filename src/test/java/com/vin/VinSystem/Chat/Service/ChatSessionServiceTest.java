package com.vin.VinSystem.Chat.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

class ChatSessionServiceTest {

    ChatSessionRepository sessionRepository = mock(ChatSessionRepository.class);
    UserRepository        userRepository    = mock(UserRepository.class);
    ChatSessionService    service;

    @BeforeEach
    void setUp() {
        service = new ChatSessionService();
        ReflectionTestUtils.setField(service, "sessionRepository", sessionRepository);
        ReflectionTestUtils.setField(service, "userRepository",    userRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private User mockUser(Long id) {
        User u = new User();
        u.setUserId(id);
        u.setUsername("user" + id);
        return u;
    }

    private ChatSession mockSession(String type, String status) {
        ChatSession s = new ChatSession();
        s.setSessionId(1L);
        s.setType(type);
        s.setStatus(status);
        return s;
    }

    // ── getOrCreateSession — null customerId ──────────────────────────────

    @Test
    void nullCustomerId_returnsNull() {
        ChatSession result = service.getOrCreateSession(null, "BOT");

        assertThat(result).isNull();
        verifyNoInteractions(sessionRepository, userRepository);
    }

    // ── getOrCreateSession — STAFF ────────────────────────────────────────

    @Test
    void staff_existingSession_returnsExisting() {
        ChatSession existing = mockSession("STAFF", "WAITING");
        when(sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(3L, "STAFF", "CLOSED"))
            .thenReturn(existing);

        ChatSession result = service.getOrCreateSession(3L, "STAFF");

        assertThat(result.getStatus()).isEqualTo("WAITING");
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void staff_noExisting_createsNew() {
        when(sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(3L, "STAFF", "CLOSED"))
            .thenReturn(null);
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockUser(3L)));

        ChatSession saved = mockSession("STAFF", "WAITING");
        when(sessionRepository.save(any())).thenReturn(saved);

        ChatSession result = service.getOrCreateSession(3L, "STAFF");

        assertThat(result.getType()).isEqualTo("STAFF");
        assertThat(result.getStatus()).isEqualTo("WAITING");
        verify(sessionRepository).save(any());
    }

    @Test
    void staff_noExisting_userNotFound_createsWithNullCustomer() {
        when(sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(99L, "STAFF", "CLOSED"))
            .thenReturn(null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ChatSession saved = mockSession("STAFF", "WAITING");
        when(sessionRepository.save(any())).thenReturn(saved);

        ChatSession result = service.getOrCreateSession(99L, "STAFF");

        assertThat(result).isNotNull();
        verify(sessionRepository).save(any());
    }

    // ── getOrCreateSession — BOT ──────────────────────────────────────────

    @Test
    void bot_existingSession_returnsExisting() {
        ChatSession existing = mockSession("BOT", "ACTIVE");
        when(sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(3L, "BOT", "CLOSED"))
            .thenReturn(existing);

        ChatSession result = service.getOrCreateSession(3L, "BOT");

        assertThat(result.getType()).isEqualTo("BOT");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void bot_noExisting_createsNew() {
        when(sessionRepository.findByCustomerUserIdAndTypeAndStatusNot(3L, "BOT", "CLOSED"))
            .thenReturn(null);
        when(userRepository.findById(3L)).thenReturn(Optional.of(mockUser(3L)));

        ChatSession saved = mockSession("BOT", "ACTIVE");
        when(sessionRepository.save(any())).thenReturn(saved);

        ChatSession result = service.getOrCreateSession(3L, "BOT");

        assertThat(result.getType()).isEqualTo("BOT");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(sessionRepository).save(any());
    }

    // ── getSessionsByStaff ────────────────────────────────────────────────

    @Test
    void getSessionsByStaff_success() {
        List<ChatSession> sessions = List.of(
            mockSession("STAFF", "ACTIVE"),
            mockSession("STAFF", "WAITING")
        );
        when(sessionRepository.findSessionsByStaffId(7L)).thenReturn(sessions);

        List<ChatSession> result = service.getSessionsByStaff(7L);

        assertThat(result).hasSize(2);
        verify(sessionRepository).findSessionsByStaffId(7L);
    }

    @Test
    void getSessionsByStaff_empty() {
        when(sessionRepository.findSessionsByStaffId(99L)).thenReturn(List.of());

        assertThat(service.getSessionsByStaff(99L)).isEmpty();
    }
}