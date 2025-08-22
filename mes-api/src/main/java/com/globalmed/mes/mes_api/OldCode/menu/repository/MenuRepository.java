package com.globalmed.mes.mes_api.OldCode.menu.repository;

import com.globalmed.mes.mes_api.OldCode.menu.domain.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    /** 활성화된 메뉴만 조회 */
    List<MenuEntity> findByIsActiveAndIsDeleted(Byte isActive, Byte isDeleted);
}