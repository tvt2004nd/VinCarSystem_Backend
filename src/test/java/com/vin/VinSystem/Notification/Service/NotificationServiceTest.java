package com.vin.VinSystem.Notification.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Service.MailService;
import com.vin.VinSystem.Notification.Entity.Notification;
import com.vin.VinSystem.Notification.Repository.NotificationRepository;

import jakarta.mail.internet.MimeMessage;

class NotificationServiceTest {

    NotificationRepository notificationRepository = mock(NotificationRepository.class);
    UserRepository         userRepository         = mock(UserRepository.class);
    JavaMailSender         mailSender             = mock(JavaMailSender.class);
    SimpMessagingTemplate  messagingTemplate      = mock(SimpMessagingTemplate.class);
    MailService            mailService            = mock(MailService.class); // ← thêm dòng này
    
    NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(
            notificationRepository,
            userRepository,
            mailSender,
            messagingTemplate,
            mailService
        );
    }
    // ── Helpers ───────────────────────────────────────────────────────────

    private User mockUser() {
        User u = new User();
        u.setUserId(3L);
        u.setUsername("tien123");
        u.setName("Trần Văn Tiền");
        u.setEmail("tien@test.com");
        return u;
    }

    private Notification mockNotification(User user) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle("Test");
        n.setMessage("Message");
        n.setType("TEST");
        n.setIsRead(false);
        return n;
    }

    // ── createNotification (User) ─────────────────────────────────────────

    @Test
    void createNotification_user_success() {
        User user = mockUser();
        Notification saved = mockNotification(user);

        when(notificationRepository.save(any())).thenReturn(saved);

        Notification result = service.createNotification(user, "Tiêu đề", "Nội dung", "CHAT", "1");

        assertThat(result.getIsRead()).isFalse();
        verify(notificationRepository).save(any());
        verify(messagingTemplate).convertAndSend(
            eq("/topic/notifications/3"), any(Object.class)
        );
    }

    // ── createNotification (userId) ───────────────────────────────────────

    @Test
    void createNotification_userId_found() {
        User user = mockUser();
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any())).thenReturn(mockNotification(user));

        assertThatNoException().isThrownBy(
            () -> service.createNotification(3L, "Title", "Msg", "TYPE", "1")
        );
        verify(notificationRepository).save(any());
    }

    @Test
    void createNotification_userId_notFound_silentSkip() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(
            () -> service.createNotification(99L, "T", "M", "T", "1")
        );
        verify(notificationRepository, never()).save(any());
    }

    // ── notifyCarReady ────────────────────────────────────────────────────

    @Test
    void notifyCarReady_success() {
        User user = mockUser();
        when(notificationRepository.save(any())).thenReturn(mockNotification(user));

        assertThatNoException().isThrownBy(
            () -> service.notifyCarReady(user, 1L, "VF8", "Chi nhánh HN")
        );

        verify(notificationRepository).save(any());
        verify(mailService).sendEmail(any(), any(), any());
    }

    // ── notifyDepositCompleted ────────────────────────────────────────────

    @Test
    void notifyDepositCompleted_noPdf_sendsSimpleEmail() {
        User user = mockUser();
        when(notificationRepository.save(any())).thenReturn(mockNotification(user));

        service.notifyDepositCompleted(user, 1L, "VF8", 1_000_000_000.0, null);

        verify(mailService).sendEmail(any(), any(), any());
    }

    @Test
    void notifyDepositCompleted_withPdf_sendsMimeEmail() {
        User user = mockUser();
        when(notificationRepository.save(any())).thenReturn(mockNotification(user));

        MimeMessage mime = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mime);

        service.notifyDepositCompleted(user, 1L, "VF8", 1_000_000_000.0, new byte[]{1, 2, 3});

       verify(mailService).sendEmailWithPdf(any(), any(), any(), any(), any());
    }

    // ── notifyFullPaymentCompleted ────────────────────────────────────────

    @Test
    void notifyFullPaymentCompleted_withPdf() {
        User user = mockUser();
        when(notificationRepository.save(any())).thenReturn(mockNotification(user));

        MimeMessage mime = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mime);

        service.notifyFullPaymentCompleted(user, 1L, 10L, "VF8", 500_000_000.0, new byte[]{1});

        verify(mailService).sendEmailWithPdf(any(), any(), any(), any(), any());
        verify(notificationRepository).save(any());
    }

    // ── sendEmail ─────────────────────────────────────────────────────────

    @Test
    void sendEmail_success() {
        service.sendEmail("tien@test.com", "Subject", "Body");

       verify(mailService).sendEmail(any(), any(), any());
    }

    @Test
    void sendEmail_emptyTo_skip() {
        service.sendEmail("", "Subject", "Body");
        service.sendEmail(null, "Subject", "Body");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // ── sendEmailWithPdf ──────────────────────────────────────────────────

    @Test
    void sendEmailWithPdf_success() {
        MimeMessage mime = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mime);

        service.sendEmailWithPdf("tien@test.com", "Subject", "Body",
            new byte[]{1, 2, 3}, "file.pdf");

       verify(mailService).sendEmailWithPdf(any(), any(), any(), any(), any());
    }

    @Test
    void sendEmailWithPdf_emptyTo_skip() {
        service.sendEmailWithPdf(null, "Sub", "Body", new byte[]{1}, "file.pdf");

        verify(mailSender, never()).createMimeMessage();
    }

    // ── getUnreadByUser / getAllByUser ────────────────────────────────────

    @Test
    void getUnreadByUser_success() {
        User user = mockUser();
        when(notificationRepository.findByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(3L))
            .thenReturn(List.of(mockNotification(user)));

        List<Notification> result = service.getUnreadByUser(3L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsRead()).isFalse();
    }

    @Test
    void getAllByUser_success() {
        User user = mockUser();
        when(notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(3L))
            .thenReturn(List.of(mockNotification(user), mockNotification(user)));

        assertThat(service.getAllByUser(3L)).hasSize(2);
    }

    // ── markRead ──────────────────────────────────────────────────────────

    @Test
    void markRead_success() {
        Notification n = mockNotification(mockUser());
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        service.markRead(1L);

        assertThat(n.getIsRead()).isTrue();
        assertThat(n.getReadAt()).isNotNull();
        verify(notificationRepository).save(n);
    }

    @Test
    void markRead_notFound_noOp() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> service.markRead(99L));
        verify(notificationRepository, never()).save(any());
    }

    // ── markAllRead ───────────────────────────────────────────────────────

    @Test
    void markAllRead_success() {
        User user = mockUser();
        Notification n1 = mockNotification(user);
        Notification n2 = mockNotification(user);

        when(notificationRepository.findByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(3L))
            .thenReturn(List.of(n1, n2));

        service.markAllRead(3L);

        assertThat(n1.getIsRead()).isTrue();
        assertThat(n2.getIsRead()).isTrue();
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void markAllRead_noUnread_noOp() {
        when(notificationRepository.findByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(3L))
            .thenReturn(List.of());

        service.markAllRead(3L);

        verify(notificationRepository).saveAll(List.of());
    }
}