package com.vin.VinSystem.Auth.Service;

import com.vin.VinSystem.Auth.DTO.RoleDTO;
import com.vin.VinSystem.Auth.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(r -> new RoleDTO(r.getRoleId(), r.getRoleName()))
                .collect(Collectors.toList());
    }
}