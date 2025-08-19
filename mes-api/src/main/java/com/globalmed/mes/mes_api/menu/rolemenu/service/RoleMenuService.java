package com.globalmed.mes.mes_api.menu.rolemenu.service;

import com.globalmed.mes.mes_api.menu.dto.MenuDto;
import com.globalmed.mes.mes_api.menu.rolemenu.domain.RoleMenuEntity;
import com.globalmed.mes.mes_api.menu.rolemenu.repository.RoleMenuRepository;
import com.globalmed.mes.mes_api.user.userrole.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;
    private final UserRoleService userRoleService;

    /** 사용자 권한 기반 메뉴 조회 */
    public List<MenuDto> getMenusByUsername(String username) {
        // 1) 사용자 역할 조회 (username 기준)
        List<String> roles = userRoleService.getRolesByUsername(username);

        // 2) 권한 있는 메뉴 조회
        List<RoleMenuEntity> roleMenus = roleMenuRepository.findByRoleCodesWithRead(roles);

        // 3) DTO로 변환 (중복 제거)
        return roleMenus.stream()
                .map(rm -> new MenuDto(rm.getMenu().getMenuCode(), rm.getMenu().getMenuName()))
                .distinct()
                .collect(Collectors.toList());
    }
}