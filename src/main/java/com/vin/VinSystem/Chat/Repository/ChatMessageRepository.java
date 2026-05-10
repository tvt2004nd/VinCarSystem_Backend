package com.vin.VinSystem.Chat.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Chat.Entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionSessionIdOrderByCreatedAtAsc(Long sessionId);


    /*
     ========================
     COUNT TODAY
     ========================
     */
    @Query(
        value = """
            SELECT COUNT(*)
            FROM chat_messages
            WHERE DATE(created_at) = CURDATE()
        """,
        nativeQuery = true
    )
    Long countToday();

    /*
     ========================
     MESSAGE BY SESSION TYPE
     ========================
     */
    @Query("""
        SELECT s.type, COUNT(m)
        FROM ChatMessage m
        JOIN m.session s
        GROUP BY s.type
    """)
    List<Object[]> countBySessionType();

    /*
     ========================
     MESSAGE BY SENDER TYPE
     ========================
     */
    @Query("""
        SELECT m.senderType, COUNT(m)
        FROM ChatMessage m
        GROUP BY m.senderType
    """)
    List<Object[]> countBySenderType();

    /*
     ========================
     STAFF MESSAGE COUNT
     ========================
     */
    @Query("""
        SELECT m.senderId, COUNT(m)
        FROM ChatMessage m
        WHERE m.senderType = 'STAFF'
        GROUP BY m.senderId
        ORDER BY COUNT(m) DESC
    """)
    List<Object[]> countMessagesByStaff();

    /*
     ========================
     SESSION BY STAFF
     ========================
     */
    @Query("""
        SELECT s.staff.userId, COUNT(s)
        FROM ChatSession s
        WHERE s.staff IS NOT NULL
        GROUP BY s.staff.userId
        ORDER BY COUNT(s) DESC
    """)
    List<Object[]> countSessionsByStaff();

    /*
     ========================
     MESSAGE LAST 7 DAYS
     ========================
     */
    @Query(
        value = """
            SELECT DATE(created_at) as date, COUNT(*) as count
            FROM chat_messages
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
            GROUP BY DATE(created_at)
            ORDER BY DATE(created_at)
        """,
        nativeQuery = true
    )
    List<Object[]> countByDayLast7Days();

    /*
     ========================
     MESSAGE COUNT BY SESSION
     (FIX N+1 QUERY)
     ========================
     */
    @Query("""
        SELECT m.session.sessionId, COUNT(m)
        FROM ChatMessage m
        GROUP BY m.session.sessionId
    """)
    List<Object[]> countMessagesGroupBySession();
     @Modifying
    @Transactional
    @Query("UPDATE ChatMessage m SET m.recalled = true, m.messageText = '[Tin nhắn đã thu hồi]' WHERE m.messageId = :messageId")
    void recallMessage(Long messageId);
 
    // Xóa hoàn toàn khỏi DB
    @Modifying
    @Transactional
    void deleteByMessageId(Long messageId);
}