package com.globalmed.mes.mes_api.equipment.equipmentstatus.repository;

import com.globalmed.mes.mes_api.equipment.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipment.equipmentstatus.domain.EquipmentStatusLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentStatusLogRepository extends JpaRepository<EquipmentStatusLogEntity, Long> {
    List<EquipmentStatusLogEntity> findByEquipmentOrderByStartTimeDesc(EquipmentEntity equipment);
}