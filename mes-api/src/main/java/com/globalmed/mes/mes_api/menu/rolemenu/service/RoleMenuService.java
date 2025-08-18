package com.globalmed.mes.mes_api.menu.rolemenu.service;

import com.globalmed.mes.mes_api.menu.rolemenu.domain.RoleMenuEntity;
import com.globalmed.mes.mes_api.menu.rolemenu.repository.RoleMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleMenuService {
    private final RoleMenuRepository roleMenuRepository;

    public Optional<RoleMenuEntity> findByRoleAndMenu(com.globalmed.mes.mes_api.role.domain.RoleEntity role,
                                                      com.globalmed.mes.mes_api.menu.domain.MenuEntity menu) {
        return roleMenuRepository.findByRoleAndMenu(role, menu);
    }

    public RoleMenuEntity save(RoleMenuEntity roleMenu) {
        return roleMenuRepository.save(roleMenu);
    }

    public List<RoleMenuEntity> findAll() {
        return roleMenuRepository.findAll();
    }
}
