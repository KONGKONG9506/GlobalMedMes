package com.globalmed.mes.mes_api.OldCode.equipment.equipmentstatuslog.repository;

import com.globalmed.mes.mes_api.OldCode.equipment.equipmentstatuslog.domain.EquipmentStatusLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentStatusLogRepository extends JpaRepository<EquipmentStatusLogEntity, Long> {
    List<EquipmentStatusLogEntity> findByEquipment_EquipmentIdOrderByStartTimeDesc(String equipmentId);
}