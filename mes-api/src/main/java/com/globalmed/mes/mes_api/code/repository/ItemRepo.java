package com.globalmed.mes.mes_api.code.repository;

import com.globalmed.mes.mes_api.code.domain.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<ItemEntity, String> {
}
