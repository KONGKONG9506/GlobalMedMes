package com.globalmed.mes.mes_api.userrole.repository;

import com.globalmed.mes.mes_api.userrole.domain.UserRoleEntity;
import com.globalmed.mes.mes_api.user.domain.UserEntity;
import com.globalmed.mes.mes_api.role.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByUserAndRole(UserEntity user, RoleEntity role);
}