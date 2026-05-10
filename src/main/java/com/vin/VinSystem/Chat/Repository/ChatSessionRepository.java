package com.vin.VinSystem.Chat.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vin.VinSystem.Chat.Entity.ChatSession;

import jakarta.persistence.LockModeType;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    ChatSession findByCustomerUserIdAndTypeAndStatusNot(Long customerId, String type, String status);

    ChatSession findByCustomerUserIdAndType(Long customerId, String type);

    List<ChatSession> findByStatus(String status);

    long countByStatus(String status);

    /*
     ========================
     STAFF SESSIONS
     ========================
     */
    @Query("""
        SELECT s
        FROM ChatSession s
        WHERE s.type = 'STAFF'
    """)
    List<ChatSession> findStaffSessions();
     @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ChatSession s WHERE s.sessionId = :id")
    Optional<ChatSession> findByIdForUpdate(@Param("id") Long id);

    // Query mới: chỉ lấy session thực sự chưa có staff
    @Query("SELECT s FROM ChatSession s WHERE s.status = 'WAITING' AND s.staff IS NULL")
    List<ChatSession> findUnassignedWaitingSessions();

 @Query("SELECT s FROM ChatSession s WHERE s.staff.userId = :staffId AND s.type = 'STAFF'")
List<ChatSession> findSessionsByStaffId(@Param("staffId") Long staffId);


    /*
     ========================
     SESSION BY TYPE
     ========================
     */
    @Query("""
        SELECT s.type, COUNT(s)
        FROM ChatSession s
        GROUP BY s.type
    """)
    List<Object[]> countByType();

    /*
     ========================
     SESSION BY STATUS
     ========================
     */
    @Query("""
        SELECT s.status, COUNT(s)
        FROM ChatSession s
        GROUP BY s.status
    """)
    List<Object[]> countByStatusGroup();

    /*
     ========================
     SESSION TODAY
     ========================
     */
    @Query(
        value = """
            SELECT COUNT(*)
            FROM chat_sessions
            WHERE DATE(created_at) = CURDATE()
        """,
        nativeQuery = true
    )
    Long countToday();

    /*
     ========================
     SESSION LAST 7 DAYS
     ========================
     */
    @Query(
        value = """
            SELECT DATE(created_at) as date, COUNT(*) as count
            FROM chat_sessions
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
            GROUP BY DATE(created_at)
            ORDER BY DATE(created_at)
        """,
        nativeQuery = true
    )
    List<Object[]> countSessionsByDayLast7Days();
}