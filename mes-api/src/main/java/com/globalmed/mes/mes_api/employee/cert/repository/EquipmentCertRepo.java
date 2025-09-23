package com.globalmed.mes.mes_api.employee.cert.repository;

import com.globalmed.mes.mes_api.employee.cert.domain.EquipmentCertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentCertRepo extends JpaRepository<EquipmentCertEntity, Long> {
    List<EquipmentCertEntity> findByEquipment_EquipmentIdAndDeletedFalse(String equipmentId);
}
