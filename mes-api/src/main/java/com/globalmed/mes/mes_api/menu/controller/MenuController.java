package com.globalmed.mes.mes_api.menu.controller;

import com.globalmed.mes.mes_api.menu.domain.MenuEntity;
import com.globalmed.mes.mes_api.menu.dto.MenuDto;
import com.globalmed.mes.mes_api.menu.rolemenu.service.RoleMenuService;
import com.globalmed.mes.mes_api.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final RoleMenuService roleMenuService;

    @GetMapping("/my")
    public ResponseEntity<List<MenuDto>> getMyMenus(@AuthenticationPrincipal String username) {
        List<MenuDto> menus = roleMenuService.getMenusByUsername(username);
        return ResponseEntity.ok(menus);
    }
}