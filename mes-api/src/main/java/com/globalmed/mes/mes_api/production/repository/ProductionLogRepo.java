package com.globalmed.mes.mes_api.production.repository;

import com.globalmed.mes.mes_api.production.domain.ProductionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductionLogRepo extends JpaRepository<ProductionLogEntity, Long> {
    Optional<ProductionLogEntity> findTopByEquipmentIdAndEventType_CodeOrderByLogIdDesc(
            String equipmentId, String eventCode);
}
