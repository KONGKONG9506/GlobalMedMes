package com.globalmed.mes.mes_api.userrole.controller;

import com.globalmed.mes.mes_api.userrole.domain.UserRoleEntity;
import com.globalmed.mes.mes_api.userrole.dto.UserRoleDto;
import com.globalmed.mes.mes_api.userrole.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    // 모든 UserRole 조회
    @GetMapping
    public List<UserRoleDto> getAllUserRoles() {
        return userRoleService.findAll().stream()
                .map(ur -> new UserRoleDto(
                        ur.getUserRoleId(),
                        ur.getUser().getUserId(),
                        ur.getRole().getRoleId()
                ))
                .collect(Collectors.toList());
    }

    // 새로운 UserRole 저장
    @PostMapping
    public UserRoleDto createUserRole(@RequestBody UserRoleEntity userRole) {
        UserRoleEntity saved = userRoleService.save(userRole);
        return new UserRoleDto(
                saved.getUserRoleId(),
                saved.getUser().getUserId(),
                saved.getRole().getRoleId()
        );
    }
}