package com.globalmed.mes.mes_api.OldCode.menu.rolemenu.repository;


import com.globalmed.mes.mes_api.OldCode.menu.rolemenu.domain.RoleMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, Long> {

    @Query("SELECT rm FROM RoleMenuEntity rm " +
            "WHERE rm.role.roleCode IN :roles " +
            "AND rm.allowRead = 1 " +
            "AND rm.isDeleted = 0")
    List<RoleMenuEntity> findByRoleCodesWithRead(@Param("roles") List<String> roles);
}
