package com.globalmed.mes.mes_api.rolemenu.controller;

import com.globalmed.mes.mes_api.rolemenu.domain.RoleMenuEntity;
import com.globalmed.mes.mes_api.rolemenu.dto.RoleMenuDto;
import com.globalmed.mes.mes_api.rolemenu.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role-menus")
@RequiredArgsConstructor
public class RoleMenuController {

    private final RoleMenuService roleMenuService;

    // 모든 RoleMenu 조회
    @GetMapping
    public List<RoleMenuDto> getAllRoleMenus() {
        return roleMenuService.findAll().stream()
                .map(rm -> new RoleMenuDto(
                        rm.getRoleMenuId(),
                        rm.getRole().getRoleId(),
                        rm.getMenu().getMenuId(),
                        rm.getAllowRead() == 1,
                        rm.getAllowWrite() == 1,
                        rm.getAllowExec() == 1
                ))
                .collect(Collectors.toList());
    }

    // 새로운 RoleMenu 저장
    @PostMapping
    public RoleMenuDto createRoleMenu(@RequestBody RoleMenuEntity roleMenu) {
        RoleMenuEntity saved = roleMenuService.save(roleMenu);
        return new RoleMenuDto(
                saved.getRoleMenuId(),
                saved.getRole().getRoleId(),
                saved.getMenu().getMenuId(),
                saved.getAllowRead() == 1,
                saved.getAllowWrite() == 1,
                saved.getAllowExec() == 1
        );
    }
}