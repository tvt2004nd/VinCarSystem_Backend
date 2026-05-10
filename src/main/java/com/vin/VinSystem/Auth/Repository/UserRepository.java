package com.vin.VinSystem.Auth.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vin.VinSystem.Auth.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
        Page<User> findDistinctByUserRoles_Role_RoleNameAndNameContainingIgnoreCaseOrUserRoles_Role_RoleNameAndPhoneNumberContaining(
            String roleName1,
            String name,
            String roleName2,
            String phone,
            Pageable pageable
    );
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findFirstByPhoneNumber(String phoneNumber);
 
}