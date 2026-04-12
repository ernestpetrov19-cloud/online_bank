package com.example.online_bank.service;

import com.example.online_bank.domain.entity.Role;
import com.example.online_bank.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role %s not found".formatted(name)));
    }

    public void create(String roleName) {
        Role role = Role.builder().name(roleName).build();
        roleRepository.save(role);
    }
}
