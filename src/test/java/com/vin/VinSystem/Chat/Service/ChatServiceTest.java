package com.vin.VinSystem.Chat.Service;

import java.util.List;
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

import com.vin.VinSystem.Chat.Entity.ChatMessage;
import com.vin.VinSystem.Chat.Entity.ChatMessage.MessageType;
import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatMessageRepository;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

class ChatServiceTest {

    ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
    ChatSessionRepository chatSessionRepository = mock(ChatSessionRepository.class);
    ChatService           service;

    @BeforeEach
    void setUp() {
        service = new ChatService();
        ReflectionTestUtils.setField(service, "chatMessageRepository", chatMessageRepository);
        ReflectionTestUtils.setField(service, "chatSessionRepository", chatSessionRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private ChatSession mockSession() {
        ChatSession s = new ChatSession();
        s.setSessionId(1L);
        s.setType("STAFF");
        s.setStatus("ACTIVE");
        return s;
    }

    private ChatMessage mockTextMessage(ChatSession session) {
        ChatMessage m = new ChatMessage();
        m.setSession(session);
        m.setSenderType("CUSTOMER");
        m.setSenderId(3L);
        m.setMessageText("hello");
        m.setMessageType(MessageType.TEXT);
        return m;
    }

    // ── getSession ────────────────────────────────────────────────────────

    @Test
    void getSession_found() {
        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(mockSession()));

        ChatSession result = service.getSession(1L);

        assertThat(result.getSessionId()).isEqualTo(1L);
    }

    @Test
    void getSession_notFound_throws() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSession(99L))
            .isInstanceOf(NoSuchElementException.class);
    }

    // ── saveMessage ───────────────────────────────────────────────────────

    @Test
    void saveMessage_success() {
        ChatSession session = mockSession();
        ChatMessage saved = mockTextMessage(session);

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any())).thenReturn(saved);

        ChatMessage result = service.saveMessage(1L, "CUSTOMER", 3L, "hello");

        assertThat(result.getMessageType()).isEqualTo(MessageType.TEXT);
        assertThat(result.getMessageText()).isEqualTo("hello");
        assertThat(result.getSenderType()).isEqualTo("CUSTOMER");
        verify(chatMessageRepository).save(any());
    }

    @Test
    void saveMessage_sessionNotFound_throws() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveMessage(99L, "CUSTOMER", 1L, "hi"))
            .isInstanceOf(NoSuchElementException.class);
    }

    // ── saveFileMessage ───────────────────────────────────────────────────

    @Test
    void saveFileMessage_image_setsImageType() {
        ChatSession session = mockSession();
        ChatMessage saved = new ChatMessage();
        saved.setMessageType(MessageType.IMAGE);
        saved.setFileName("photo.png");
        saved.setFileUrl("http://cdn/photo.png");

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any())).thenReturn(saved);

        ChatMessage result = service.saveFileMessage(
            1L, "CUSTOMER", 3L, "Tiền",
            "http://cdn/photo.png", "photo.png", 204800L, "image/png"
        );

        assertThat(result.getMessageType()).isEqualTo(MessageType.IMAGE);
        assertThat(result.getFileName()).isEqualTo("photo.png");
        verify(chatMessageRepository).save(any());
    }

    @Test
    void saveFileMessage_nonImage_setsFileType() {
        ChatSession session = mockSession();
        ChatMessage saved = new ChatMessage();
        saved.setMessageType(MessageType.FILE);
        saved.setFileName("report.pdf");

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any())).thenReturn(saved);

        ChatMessage result = service.saveFileMessage(
            1L, "STAFF", 7L, "Tuấn",
            "http://cdn/report.pdf", "report.pdf", 512000L, "application/pdf"
        );

        assertThat(result.getMessageType()).isEqualTo(MessageType.FILE);
    }

    @Test
    void saveFileMessage_nullMime_setsFileType() {
        ChatSession session = mockSession();
        ChatMessage saved = new ChatMessage();
        saved.setMessageType(MessageType.FILE);

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any())).thenReturn(saved);

        ChatMessage result = service.saveFileMessage(
            1L, "CUSTOMER", 3L, "Tiền",
            "http://cdn/file", "file.zip", 1024L, null
        );

        assertThat(result.getMessageType()).isEqualTo(MessageType.FILE);
    }

    @Test
    void saveFileMessage_sessionNotFound_throws() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveFileMessage(
            99L, "CUSTOMER", 1L, "name",
            "url", "file.png", 100L, "image/png"
        )).isInstanceOf(NoSuchElementException.class);
    }

    // ── getMessages ───────────────────────────────────────────────────────

    @Test
    void getMessages_success() {
        ChatSession session = mockSession();
        List<ChatMessage> messages = List.of(
            mockTextMessage(session),
            mockTextMessage(session)
        );
        when(chatMessageRepository.findBySessionSessionIdOrderByCreatedAtAsc(1L))
            .thenReturn(messages);

        List<ChatMessage> result = service.getMessages(1L);

        assertThat(result).hasSize(2);
        verify(chatMessageRepository).findBySessionSessionIdOrderByCreatedAtAsc(1L);
    }

    @Test
    void getMessages_empty() {
        when(chatMessageRepository.findBySessionSessionIdOrderByCreatedAtAsc(1L))
            .thenReturn(List.of());

        assertThat(service.getMessages(1L)).isEmpty();
    }
}