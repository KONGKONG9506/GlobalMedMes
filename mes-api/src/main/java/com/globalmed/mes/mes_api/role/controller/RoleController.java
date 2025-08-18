package com.globalmed.mes.mes_api.role.controller;

import com.globalmed.mes.mes_api.role.domain.RoleEntity;
import com.globalmed.mes.mes_api.role.dto.RoleDto;
import com.globalmed.mes.mes_api.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 모든 Role 조회
    @GetMapping
    public List<RoleDto> getAllRoles() {
        return roleService.findAll().stream()
                .map(r -> new RoleDto(
                        r.getRoleId(),
                        r.getRoleCode(),
                        r.getRoleName(),
                        r.getDescription()
                ))
                .collect(Collectors.toList());
    }

    // Role 생성
    @PostMapping
    public RoleDto createRole(@RequestBody RoleEntity roleEntity) {
        RoleEntity saved = roleService.save(roleEntity);
        return new RoleDto(
                saved.getRoleId(),
                saved.getRoleCode(),
                saved.getRoleName(),
                saved.getDescription()
        );
    }
}