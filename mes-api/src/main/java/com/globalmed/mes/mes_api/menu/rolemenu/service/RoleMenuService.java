package com.globalmed.mes.mes_api.menu.rolemenu.service;

import com.globalmed.mes.mes_api.menu.rolemenu.domain.RoleMenuEntity;
import com.globalmed.mes.mes_api.menu.rolemenu.repository.RoleMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleMenuService {
    private final RoleMenuRepository roleMenuRepository;

    public RoleMenuEntity save(RoleMenuEntity roleMenu) {
        return roleMenuRepository.save(roleMenu);
    }

    public List<RoleMenuEntity> findByRoleId(Long roleId) {
        return roleMenuRepository.findByRole_RoleId(roleId);
    }

    public List<RoleMenuEntity> findByMenuId(Long menuId) {
        return roleMenuRepository.findByMenu_MenuId(menuId);
    }

    public Optional<RoleMenuEntity> findByRoleIdAndMenuId(Long roleId, Long menuId) {
        return roleMenuRepository.findByRole_RoleIdAndMenu_MenuId(roleId, menuId);
    }
}