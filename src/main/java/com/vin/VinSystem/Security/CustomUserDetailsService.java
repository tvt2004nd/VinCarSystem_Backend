package com.vin.VinSystem.Security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Entity.UserRole;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Auth.Repository.UserRoleRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

  @Override
public UserDetails loadUserByUsername(String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<UserRole> userRoles =
            userRoleRepository.findByUserWithRole(user);

    List<GrantedAuthority> authorities =
            userRoles.stream()
                    .map(ur ->
                            new SimpleGrantedAuthority(
                                    "ROLE_" + ur.getRole().getRoleName()
                            )
                    )
                    .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
    );
}
}