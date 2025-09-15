package com.globalmed.mes.mes_api.kpi.service;

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
public class DailyKpiService {

    private final KpiDataService kpiDataService;
    private final KpiDataRepo kpiDataRepo;
    private final PerformanceRepo performanceRepo;
    private final PlannedDowntimeService plannedDowntimeService;
    private final UnplannedDowntimeService unplannedDowntimeService;

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

            long totalPeriodSeconds = Duration.between(firstStartTime, lastEndTime).toSeconds();
            long plannedDowntimeSeconds = plannedDowntimeService.calculatePlannedDowntimeSeconds(list.get(0).getEquipmentId(), firstStartTime.atOffset(ZoneOffset.UTC), lastEndTime.atOffset(ZoneOffset.UTC));
            long unplannedDowntimeSeconds = unplannedDowntimeService.calculateUnplannedDowntimeSeconds(list.get(0).getEquipmentId(), firstStartTime.atOffset(ZoneOffset.UTC), lastEndTime.atOffset(ZoneOffset.UTC));

            BigDecimal plannedSeconds = BigDecimal.valueOf(totalPeriodSeconds - plannedDowntimeSeconds);
            BigDecimal totalRunSeconds = plannedSeconds.subtract(BigDecimal.valueOf(unplannedDowntimeSeconds));

            ProductionPerformanceEntity representative = list.get(0);
            Optional<KpiDataEntity> existingBatchKpi = kpiDataRepo.findDailyBatchKpi(
                    date,
                    representative.getEquipmentId(),
                    representative.getProcessId(),
                    representative.getItemId(),
                    kpiDataService.getDailyBatchAggregationTypeId(),
                    "DAILY"
            );

            KpiDataEntity batchKpi = existingBatchKpi.orElseGet(KpiDataEntity::new);

            batchKpi.setKpiDate(date);
            batchKpi.setEquipmentId(representative.getEquipmentId());
            batchKpi.setProcessId(representative.getProcessId());
            batchKpi.setItemId(representative.getItemId());
            batchKpi.setAggregationTypeId(kpiDataService.getDailyBatchAggregationTypeId());
            batchKpi.setBatchGroupKey("DAILY");
            batchKpi.setStartTime(firstStartTime);
            batchKpi.setEndTime(lastEndTime);

            // 공통 서비스 호출하여 계산 및 저장
            kpiDataService.calculateAndSave(batchKpi, totalGoodQty, totalDefectQty, totalRunSeconds, plannedSeconds);
        });
    }
}
