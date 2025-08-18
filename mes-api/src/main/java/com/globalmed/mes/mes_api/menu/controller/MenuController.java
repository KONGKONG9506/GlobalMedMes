package com.globalmed.mes.mes_api.menu.controller;

import com.globalmed.mes.mes_api.menu.domain.MenuEntity;
import com.globalmed.mes.mes_api.menu.dto.MenuDto;
import com.globalmed.mes.mes_api.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public List<MenuDto> getAllMenus() {
        return menuService.getAllMenus().stream()
                .map(m -> new MenuDto(
                        m.getMenuId(),
                        m.getMenuCode(),
                        m.getMenuName(),
                        m.getPath(),
                        m.getComponent(),
                        m.getIcon(),
                        m.getSortOrder(),
                        m.getIsPublic(),
                        m.getIsActive()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping
    public MenuDto createMenu(@RequestBody MenuEntity menuEntity) {
        MenuEntity saved = menuService.save(menuEntity);
        return new MenuDto(
                saved.getMenuId(),
                saved.getMenuCode(),
                saved.getMenuName(),
                saved.getPath(),
                saved.getComponent(),
                saved.getIcon(),
                saved.getSortOrder(),
                saved.getIsPublic(),
                saved.getIsActive()
        );
    }
}