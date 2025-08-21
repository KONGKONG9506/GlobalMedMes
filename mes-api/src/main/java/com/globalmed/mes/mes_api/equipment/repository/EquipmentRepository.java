package com.globalmed.mes.mes_api.equipment.repository;

import com.globalmed.mes.mes_api.equipment.domain.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<EquipmentEntity, String> {
}