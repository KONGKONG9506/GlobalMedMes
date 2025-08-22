package com.globalmed.mes.mes_api.OldCode.auth.role.repository;

import com.globalmed.mes.mes_api.OldCode.auth.role.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleCode(String roleCode);
}