package com.vin.VinSystem.Auth.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser_UserId(Long userId);

    List<UserRole> findByUser(User user);

    // 🔥 FIX LazyInitializationException
    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user = :user")
    List<UserRole> findByUserWithRole(@Param("user") User user);
}