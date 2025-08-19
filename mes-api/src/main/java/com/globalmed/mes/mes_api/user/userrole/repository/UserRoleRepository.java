package com.globalmed.mes.mes_api.user.userrole.repository;

import com.globalmed.mes.mes_api.user.userrole.domain.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, String> {
    List<UserRoleEntity> findByUser_UserId(String userId);
    List<UserRoleEntity> findByRole_RoleId(Long roleId);
    Optional<UserRoleEntity> findByUser_UserIdAndRole_RoleId(String userId, Long roleId);
}