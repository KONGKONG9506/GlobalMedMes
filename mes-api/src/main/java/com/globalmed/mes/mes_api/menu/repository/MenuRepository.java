package com.globalmed.mes.mes_api.menu.repository;

import com.globalmed.mes.mes_api.menu.domain.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    Optional<MenuEntity> findByMenuCode(String menuCode);
}