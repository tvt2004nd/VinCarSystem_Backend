package com.vin.VinSystem.Notification.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vin.VinSystem.Notification.Entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}