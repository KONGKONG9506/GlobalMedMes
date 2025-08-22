package com.globalmed.mes.mes_api.OldCode.auth.role.service;

import com.globalmed.mes.mes_api.OldCode.auth.role.domain.RoleEntity;
import com.globalmed.mes.mes_api.OldCode.auth.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public Optional<RoleEntity> findByRoleCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }

    public RoleEntity save(RoleEntity role) {
        return roleRepository.save(role);
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }
}