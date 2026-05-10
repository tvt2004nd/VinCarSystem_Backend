package com.vin.VinSystem.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vin.VinSystem.Auth.Entity.Role;
import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;
import com.vin.VinSystem.Branch.Repository.BranchRepository;

@Configuration
public class DataInitializer {

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.email:admin@vin.com}")
    private String adminEmail;

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               UserRoleRepository userRoleRepository,
                               PasswordEncoder passwordEncoder,
                               BranchRepository branchRepository) {
        return args -> {

            // ── Tạo Roles ────────────────────────────────────────────────
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role(); r.setRoleName("ADMIN");
                        return roleRepository.save(r);
                    });

            for (String name : new String[]{"STAFF", "CUSTOMER", "MANAGER"}) {
                roleRepository.findByRoleName(name).orElseGet(() -> {
                    Role r = new Role(); r.setRoleName(name);
                    return roleRepository.save(r);
                });
            }

            // ── Tạo tài khoản Admin ───────────────────────────────────────
            if (!userRepository.existsByUsername(adminUsername)) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setEmail(adminEmail);
                admin.setName("System Admin");

                User savedAdmin = userRepository.save(admin);

                UserRole userRole = new UserRole();
                userRole.setUser(savedAdmin);
                userRole.setRole(adminRole);
                userRoleRepository.save(userRole);

                System.out.println(">>> ADMIN created: " + adminUsername);
            }
        };
    }
}