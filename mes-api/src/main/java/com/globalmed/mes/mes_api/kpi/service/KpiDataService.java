package com.globalmed.mes.mes_api.kpi.service;

import com.globalmed.mes.mes_api.code.CodeRepo;
import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import com.globalmed.mes.mes_api.kpi.downtime.service.PlannedDowntimeService;
import com.globalmed.mes.mes_api.kpi.downtime.service.UnplannedDowntimeService;
import com.globalmed.mes.mes_api.kpi.repository.KpiDataRepo;
import com.globalmed.mes.mes_api.performance.domain.ProductionPerformanceEntity;
import com.globalmed.mes.mes_api.performance.repository.PerformanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KpiDataService {

    private final KpiCalculationService kpiCalculationService;
    private final KpiDataRepo kpiDataRepo;
    private final PerformanceRepo performanceRepo;
    private final PlannedDowntimeService plannedDowntimeService;
    private final UnplannedDowntimeService unplannedDowntimeService;
    private final CodeRepo codeRepo;

    private static final String KPI_DATA_TYPE_GROUP = "KPI_DATA_TYPE";
    private static final String KPI_CALC_STATUS_GROUP = "KPI_CALC_STATUS";

    private Long getCodeId(String groupCode, String code) {
        return codeRepo.findByGroupCodeAndCode(groupCode, code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + groupCode + " - " + code))
                .getCodeId();
    }
    private Long getRealtimeAggregationTypeId() {
        return getCodeId(KPI_DATA_TYPE_GROUP, "REALTIME");
    }
    private Long getDailyBatchAggregationTypeId() {
        return getCodeId(KPI_DATA_TYPE_GROUP, "DAILY_BATCH");
    }
    private Long getSuccessCalcStatusCodeId() {
        return getCodeId(KPI_CALC_STATUS_GROUP, "SUCCESS");
    }

    /**
     * ProductionPerformanceEntity 기반 KPI 실시간 계산 및 저장/갱신
     * 이 메서드는 각 performance 기록에 대해 개별적인 KPI를 계산하고 저장
     */

    @Transactional
    public void saveKpiFromPerformance(ProductionPerformanceEntity p) {
        LocalDateTime now = LocalDateTime.now();

        Optional<KpiDataEntity> existingKpi = kpiDataRepo.findRealtimeKpi(
                p.getStartTime().toLocalDate(),
                p.getWorkOrderId(),
                p.getEquipmentId(),
                p.getProcessId(),
                p.getItemId(),
                getRealtimeAggregationTypeId()
        );

        KpiDataEntity kpi = existingKpi.orElseGet(KpiDataEntity::new);

        BigDecimal goodQty = p.getProducedQty().subtract(p.getDefectQty());
        BigDecimal defectQty = p.getDefectQty();
        long totalPeriodSeconds = Duration.between(p.getStartTime(), p.getEndTime()).toSeconds();

        long unplannedDowntimeMinutes = unplannedDowntimeService.calculateUnplannedDowntimeMinutes(p.getEquipmentId(), p.getStartTime().atOffset(ZoneOffset.UTC), p.getEndTime().atOffset(ZoneOffset.UTC));
        long plannedDowntimeMinutes = plannedDowntimeService.calculatePlannedDowntimeMinutes(p.getEquipmentId(), p.getStartTime().atOffset(ZoneOffset.UTC), p.getEndTime().atOffset(ZoneOffset.UTC));

        BigDecimal plannedSeconds = BigDecimal.valueOf(totalPeriodSeconds - (plannedDowntimeMinutes * 60));
        BigDecimal runSeconds =  plannedSeconds.subtract(BigDecimal.valueOf(unplannedDowntimeMinutes * 60));

        Map<String, BigDecimal> kpiValues = kpiCalculationService
                .calculateFromPerformance(goodQty, defectQty, runSeconds, plannedSeconds);

        kpi.setKpiDate(p.getStartTime().toLocalDate());
        kpi.setEquipmentId(p.getEquipmentId());
        kpi.setProcessId(p.getProcessId());
        kpi.setItemId(p.getItemId());
        kpi.setWorkOrderId(p.getWorkOrderId());
        kpi.setAggregationTypeId(getRealtimeAggregationTypeId());
        kpi.setBatchGroupKey(null);
        kpi.setStartTime(p.getStartTime());
        kpi.setEndTime(p.getEndTime());
        kpi.setCalcStatusCodeId(getSuccessCalcStatusCodeId());
        kpi.setCalcAt(now);
        kpi.setCreatedBy("system");

        kpi.setActualYield(kpiValues.get("yield"));
        kpi.setActualDefectRate(kpiValues.get("defectRate"));
        kpi.setActualOee(kpiValues.get("oee"));
        kpi.setActualProductivity(kpiValues.get("productivity"));

        kpiDataRepo.save(kpi);
    }

    /**
     * 일일 배치 KPI 계산 및 저장
     */
    @Transactional
    public void runDailyBatchKpiCalculation(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<ProductionPerformanceEntity> performances = performanceRepo.findPerformancesForDay(startOfDay, endOfDay);

        if (performances.isEmpty()) {
            return;
        }

        Map<String, List<ProductionPerformanceEntity>> groupedPerformances = performances.stream()
                .collect(Collectors.groupingBy(p -> p.getEquipmentId() + "_" + p.getProcessId() + "_" + p.getItemId()));

        groupedPerformances.forEach((key, list) -> {
            BigDecimal totalGoodQty = BigDecimal.ZERO;
            BigDecimal totalDefectQty = BigDecimal.ZERO;
            LocalDateTime firstStartTime = list.stream()
                    .map(ProductionPerformanceEntity::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime lastEndTime = list.stream()
                    .map(ProductionPerformanceEntity::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            for (ProductionPerformanceEntity p : list) {
                totalGoodQty = totalGoodQty.add(p.getProducedQty().subtract(p.getDefectQty()));
                totalDefectQty = totalDefectQty.add(p.getDefectQty());
            }

            // 워크 오더 지시 시간
            long totalPeriodSeconds = Duration.between(firstStartTime, lastEndTime).toSeconds();

            // 해당 지시 시간 사이의 계획된 비가동 시간
            long plannedDowntimeMinutes = plannedDowntimeService.calculatePlannedDowntimeMinutes(list.get(0).getEquipmentId(), firstStartTime.atOffset(ZoneOffset.UTC), lastEndTime.atOffset(ZoneOffset.UTC));

            // 계획되지 않은 비가동 시간
            long unplannedDowntimeMinutes = unplannedDowntimeService.calculateUnplannedDowntimeMinutes(list.get(0).getEquipmentId(), firstStartTime.atOffset(ZoneOffset.UTC), lastEndTime.atOffset(ZoneOffset.UTC));

            // 계획된 가동 시간 (초)
            BigDecimal plannedSeconds = BigDecimal.valueOf(totalPeriodSeconds - (plannedDowntimeMinutes * 60));

            // 총 가동 시간 (초) = 계획된 가동 시간 - 계획되지 않은 비가동 시간
            BigDecimal totalRunSeconds = plannedSeconds.subtract(BigDecimal.valueOf(unplannedDowntimeMinutes * 60));

            Map<String, BigDecimal> kpiValues = kpiCalculationService.calculateFromPerformance(
                    totalGoodQty, totalDefectQty, totalRunSeconds, plannedSeconds
            );

            ProductionPerformanceEntity representative = list.get(0);
            Optional<KpiDataEntity> existingBatchKpi = kpiDataRepo.findDailyBatchKpi(date, representative.getEquipmentId(), representative.getProcessId(), representative.getItemId(), getDailyBatchAggregationTypeId(), "DAILY");

            KpiDataEntity batchKpi = existingBatchKpi.orElseGet(KpiDataEntity::new);

            batchKpi.setKpiDate(date);
            batchKpi.setEquipmentId(representative.getEquipmentId());
            batchKpi.setProcessId(representative.getProcessId());
            batchKpi.setItemId(representative.getItemId());
            batchKpi.setAggregationTypeId(getDailyBatchAggregationTypeId());
            batchKpi.setBatchGroupKey("DAILY");

            batchKpi.setStartTime(firstStartTime);
            batchKpi.setEndTime(lastEndTime);
            batchKpi.setCalcStatusCodeId(getSuccessCalcStatusCodeId());
            batchKpi.setCalcAt(LocalDateTime.now());
            batchKpi.setCreatedBy("system");

            batchKpi.setActualYield(kpiValues.get("yield"));
            batchKpi.setActualDefectRate(kpiValues.get("defectRate"));
            batchKpi.setActualOee(kpiValues.get("oee"));
            batchKpi.setActualProductivity(kpiValues.get("productivity"));

            kpiDataRepo.save(batchKpi);
        });
    }
}
