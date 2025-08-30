package com.globalmed.mes.mes_api.kpi.repository;

import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface KpiDataRepo extends JpaRepository<KpiDataEntity, Long> {

    Optional<KpiDataEntity> findByKpiDateAndEquipmentIdAndProcessIdAndItemIdAndAggregationType(
            LocalDate kpiDate,
            String equipmentId,
            String processId,
            String itemId,
            String aggregationType
    );

    // 실시간 KPI 조회용: performanceId + kpiDate
    Optional<KpiDataEntity> findByPerformanceIdAndKpiDate(Long performanceId, LocalDate kpiDate);
}
