package com.globalmed.mes.mes_api.menu.rolemenu.repository;

import com.globalmed.mes.mes_api.menu.rolemenu.domain.RoleMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, Long> {
    List<RoleMenuEntity> findByRole_RoleId(Long roleId);
    List<RoleMenuEntity> findByMenu_MenuId(Long menuId);
    Optional<RoleMenuEntity> findByRole_RoleIdAndMenu_MenuId(Long roleId, Long menuId);
}