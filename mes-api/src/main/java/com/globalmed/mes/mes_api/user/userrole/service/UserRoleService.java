package com.globalmed.mes.mes_api.user.userrole.service;

import com.globalmed.mes.mes_api.user.service.UserService;
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
    private final UserService userService; // 추가

    /** userId 기준으로 역할 코드 목록 반환 */
    public List<String> getRolesByUserId(String userId) {
        return userRoleRepository.findByUser_UserId(userId)
                .stream()
                .map(ur -> ur.getRole().getRoleCode())
                .toList();
    }

    /** username 기준으로 역할 코드 목록 반환 */
    public List<String> getRolesByUsername(String username) {
        return userService.findByUsername(username)
                .map(user -> getRolesByUserId(user.getUserId()))
                .orElse(List.of()); // 사용자 없으면 빈 리스트 반환
    }
}