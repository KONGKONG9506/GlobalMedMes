package com.globalmed.mes.mes_api.kpi.repository;

import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface KpiDataRepo extends JpaRepository<KpiDataEntity, Long> {

    Optional<KpiDataEntity> findByKpiDateAndEquipmentIdAndProcessIdAndItemId(
            LocalDate kpiDate, String equipmentId, String processId, String itemId);

    List<KpiDataEntity> findAllByKpiDate(LocalDate kpiDate);
}
