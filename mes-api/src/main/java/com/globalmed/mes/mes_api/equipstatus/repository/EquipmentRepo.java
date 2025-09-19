package com.globalmed.mes.mes_api.equipstatus.repository;

import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepo extends JpaRepository<EquipmentEntity, String> {
}
