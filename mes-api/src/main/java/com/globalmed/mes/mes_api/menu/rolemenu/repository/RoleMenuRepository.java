package com.globalmed.mes.mes_api.menu.rolemenu.repository;


import com.globalmed.mes.mes_api.menu.rolemenu.domain.RoleMenuEntity;
import com.globalmed.mes.mes_api.role.domain.RoleEntity;
import com.globalmed.mes.mes_api.menu.domain.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, Long> {
    Optional<RoleMenuEntity> findByRoleAndMenu(RoleEntity role, MenuEntity menu);
}