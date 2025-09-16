package com.globalmed.mes.mes_api.kpi.service;

import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import com.globalmed.mes.mes_api.kpi.downtime.service.PlannedDowntimeService;
import com.globalmed.mes.mes_api.kpi.downtime.service.UnplannedDowntimeService;
import com.globalmed.mes.mes_api.kpi.repository.KpiDataRepo;
import com.globalmed.mes.mes_api.performance.domain.ProductionPerformanceEntity;
import com.globalmed.mes.mes_api.performance.repository.PerformanceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeKpiService {

    private final KpiDataService kpiDataService;
    private final KpiDataRepo kpiDataRepo;
    private final PerformanceRepo performanceRepo;
    private final PlannedDowntimeService plannedDowntimeService;
    private final UnplannedDowntimeService unplannedDowntimeService;

    @Transactional
    public void saveKpiFromPerformance(ProductionPerformanceEntity newPerformance) {
        // 1. 주어진 작업 순서에 대한 모든 성과 기록을 가져옵니다.
        List<ProductionPerformanceEntity> allPerformancesForWorkOrder = performanceRepo.findPerformancesByWorkOrder(
                newPerformance.getWorkOrderId(),
                newPerformance.getEquipmentId(),
                newPerformance.getProcessId(),
                newPerformance.getItemId()
        );

        if (allPerformancesForWorkOrder.isEmpty()) {
            return;
        }

        // 2. 모든 데이터 포인트를 축적합니다.
        BigDecimal totalGoodQty = BigDecimal.ZERO;
        BigDecimal totalDefectQty = BigDecimal.ZERO;
        LocalDateTime firstStartTime = null;
        LocalDateTime lastEndTime = null;

        for (ProductionPerformanceEntity p : allPerformancesForWorkOrder) {
            totalGoodQty = totalGoodQty.add(p.getProducedQty().subtract(p.getDefectQty()));
            totalDefectQty = totalDefectQty.add(p.getDefectQty());
            if (firstStartTime == null || p.getStartTime().isBefore(firstStartTime)) {
                firstStartTime = p.getStartTime();
            }
            if (lastEndTime == null || p.getEndTime().isAfter(lastEndTime)) {
                lastEndTime = p.getEndTime();
            }
        }

        log.info("firstStartTime : {}", firstStartTime);
        log.info("lastEndTime : {}", lastEndTime);
        // 3. 누적된 시간을 기준으로 계획된/계획되지 않은 다운타임을 계산합니다.
        long totalPeriodSeconds = Duration.between(firstStartTime, lastEndTime).toSeconds();
        long plannedDowntimeSeconds = plannedDowntimeService.calculatePlannedDowntimeSeconds(newPerformance.getEquipmentId(),
                firstStartTime,
                lastEndTime);

        long unplannedDowntimeSeconds = unplannedDowntimeService.calculateUnplannedDowntimeSeconds(newPerformance.getEquipmentId(),
                firstStartTime.atOffset(ZoneOffset.UTC),
                lastEndTime.atOffset(ZoneOffset.UTC));

        BigDecimal plannedSeconds = BigDecimal.valueOf(totalPeriodSeconds - plannedDowntimeSeconds );
        BigDecimal runSeconds = plannedSeconds.subtract(BigDecimal.valueOf(unplannedDowntimeSeconds));
        log.info("planned downtime seconds : {}", plannedDowntimeSeconds);
        log.info("unplanned downtime seconds : {}", unplannedDowntimeSeconds);
        log.info("total period seconds : {}", totalPeriodSeconds);
        log.info("planned seconds : {}", plannedSeconds);
        log.info("total run seconds : {}", runSeconds);

        // 4. 기존 KPI 기록을 찾거나 새로 만듭니다.
        Optional<KpiDataEntity> existingKpi =  kpiDataRepo.findRealtimeKpiByWorkOrderId(
                newPerformance.getWorkOrderId(),
                newPerformance.getEquipmentId(),
                newPerformance.getProcessId(),
                newPerformance.getItemId(),
                kpiDataService.getRealtimeAggregationTypeId()
        );

        KpiDataEntity kpi = existingKpi.orElseGet(KpiDataEntity::new);
        kpi.setKpiDate(newPerformance.getStartTime().toLocalDate());
        kpi.setEquipmentId(newPerformance.getEquipmentId());
        kpi.setProcessId(newPerformance.getProcessId());
        kpi.setItemId(newPerformance.getItemId());
        kpi.setWorkOrderId(newPerformance.getWorkOrderId());
        kpi.setAggregationTypeId(kpiDataService.getRealtimeAggregationTypeId());
        kpi.setBatchGroupKey(null);
        kpi.setStartTime(firstStartTime);
        kpi.setEndTime(lastEndTime);

        // 5. 공통 서비스 호출하여 계산 및 저장
        kpiDataService.calculateAndSave(kpi, totalGoodQty, totalDefectQty, runSeconds, plannedSeconds);
    }
}
