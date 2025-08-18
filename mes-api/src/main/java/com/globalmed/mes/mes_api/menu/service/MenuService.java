package com.globalmed.mes.mes_api.menu.service;

import com.globalmed.mes.mes_api.menu.domain.MenuEntity;
import com.globalmed.mes.mes_api.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public Optional<MenuEntity> findByMenuCode(String menuCode) {
        return menuRepository.findByMenuCode(menuCode);
    }

    public MenuEntity save(MenuEntity menu) {
        return menuRepository.save(menu);
    }

    public List<MenuEntity> getAllMenus() {
        return menuRepository.findAll();
    }
}