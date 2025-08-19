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

    /** 활성화된 메뉴만 조회 */
    public List<MenuEntity> getActiveMenus() {
        return menuRepository.findByIsActiveAndIsDeleted((byte)1, (byte)0);
    }
}