package com.globalmed.mes.mes_api.user.userrole.service;

import com.globalmed.mes.mes_api.user.userrole.domain.UserRoleEntity;
import com.globalmed.mes.mes_api.user.userrole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleEntity save(UserRoleEntity userRole) {
        return userRoleRepository.save(userRole);
    }

    public List<UserRoleEntity> findByUserId(String userId) {
        return userRoleRepository.findByUser_UserId(userId);
    }

    public List<UserRoleEntity> findByRoleId(Long roleId) {
        return userRoleRepository.findByRole_RoleId(roleId);
    }

    public Optional<UserRoleEntity> findByUserIdAndRoleId(String userId, Long roleId) {
        return userRoleRepository.findByUser_UserIdAndRole_RoleId(userId, roleId);
    }
}
