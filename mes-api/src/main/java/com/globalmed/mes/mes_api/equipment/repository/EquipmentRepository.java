package com.globalmed.mes.mes_api.equipment.repository;

import com.globalmed.mes.mes_api.equipment.domain.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, String> {
}