package com.globalmed.mes.mes_api.user.userrole.service;

import com.globalmed.mes.mes_api.user.userrole.domain.UserRoleEntity;
import com.globalmed.mes.mes_api.user.userrole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public Optional<UserRoleEntity> findByUserAndRole(com.globalmed.mes.mes_api.user.domain.UserEntity user,
                                                      com.globalmed.mes.mes_api.role.domain.RoleEntity role) {
        return userRoleRepository.findByUserAndRole(user, role);
    }

    public UserRoleEntity save(UserRoleEntity userRole) {
        return userRoleRepository.save(userRole);
    }

    public List<UserRoleEntity> findAll() {
        return userRoleRepository.findAll();
    }
}
