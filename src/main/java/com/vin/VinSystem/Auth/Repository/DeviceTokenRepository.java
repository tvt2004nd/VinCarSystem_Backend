package com.vin.VinSystem.Auth.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Auth.Entity.DeviceToken;
import com.vin.VinSystem.Auth.Entity.User;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByFcmToken(String fcmToken);
    List<DeviceToken> findByUser(User user);
    List<DeviceToken> findByUser_UserId(Long userId);
    void deleteByFcmToken(String fcmToken);
}
