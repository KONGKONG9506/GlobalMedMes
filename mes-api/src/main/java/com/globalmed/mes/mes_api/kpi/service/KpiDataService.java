package com.globalmed.mes.mes_api.kpi.service;

import com.globalmed.mes.mes_api.kpi.KpiDataConstants;
import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
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

    /**
     * ProductionPerformanceEntity 기반 KPI 실시간 계산 및 저장/갱신
     * 이 메서드는 각 performance 기록에 대해 개별적인 KPI를 계산하고 저장합니다.
     */
    @Transactional
    public void saveKpiFromPerformance(ProductionPerformanceEntity p) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // work_order_id를 기반으로 KPI 기록을 찾습니다.
        Optional<KpiDataEntity> existingKpi = kpiDataRepo.findRealtimeKpi(
                p.getStartTime().toLocalDate(),
                p.getWorkOrderId(),
                p.getEquipmentId(),
                p.getProcessId(),
                p.getItemId(),
                KpiDataConstants.AGG_REALTIME
        );
        KpiDataEntity kpi = existingKpi.orElseGet(KpiDataEntity::new);

        BigDecimal goodQty = p.getProducedQty().subtract(p.getDefectQty());
        BigDecimal defectQty = p.getDefectQty();
        BigDecimal runSeconds = BigDecimal.valueOf(java.time.Duration.between(p.getStartTime(), p.getEndTime()).toSeconds());

        Map<String, BigDecimal> kpiValues = kpiCalculationService.calculateFromPerformance(goodQty, defectQty, runSeconds, runSeconds);

        kpi.setKpiDate(p.getStartTime().toLocalDate());
        kpi.setEquipmentId(p.getEquipmentId());
        kpi.setProcessId(p.getProcessId());
        kpi.setItemId(p.getItemId());
        kpi.setWorkOrderId(p.getWorkOrderId());
        kpi.setAggregationType(KpiDataConstants.AGG_REALTIME);
        kpi.setBatchCheck(null);
        kpi.setStartTime(p.getStartTime());
        kpi.setEndTime(p.getEndTime());
        kpi.setCalcSuccessCheck(KpiDataConstants.CALC_SUCCESS);
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
     * 지정된 날짜의 모든 생산 실적을 집계하여 일일 배치 KPI를 생성합니다.
     */
    @Transactional
    public void runDailyBatchKpiCalculation(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // 날짜별 모든 생산 실적 데이터 조회
        List<ProductionPerformanceEntity> performances = performanceRepo.findPerformancesForDay(startOfDay, endOfDay);

        if (performances.isEmpty()) {
            return;
        }

        // 장비,공정,품목 조합별로 실적을 그룹화
        Map<String, List<ProductionPerformanceEntity>> groupedPerformances = performances.stream()
                .collect(Collectors.groupingBy(p -> p.getEquipmentId() + "_" + p.getProcessId() + "_" + p.getItemId()));

        groupedPerformances.forEach((key, list) -> {
            // 그룹별 데이터 집계
            BigDecimal totalGoodQty = BigDecimal.ZERO;
            BigDecimal totalDefectQty = BigDecimal.ZERO;
            BigDecimal totalRunSeconds = BigDecimal.ZERO;
            LocalDateTime firstStartTime = list.get(0).getStartTime();
            LocalDateTime lastEndTime = list.get(list.size() - 1).getEndTime();

            for (ProductionPerformanceEntity p : list) {
                totalGoodQty = totalGoodQty.add(p.getProducedQty().subtract(p.getDefectQty()));
                totalDefectQty = totalDefectQty.add(p.getDefectQty());
                totalRunSeconds = totalRunSeconds.add(BigDecimal.valueOf(Duration.between(p.getStartTime(), p.getEndTime()).toSeconds()));
            }

            // 배치 KPI 값 계산
            Map<String, BigDecimal> kpiValues = kpiCalculationService.calculateFromPerformance(
                    totalGoodQty, totalDefectQty, totalRunSeconds, totalRunSeconds
            );

            // 배치 KPI 엔티티 생성 또는 갱신


            ProductionPerformanceEntity representative = list.get(0);
            Optional<KpiDataEntity> existingBatchKpi = kpiDataRepo.findDailyBatchKpi(date, representative.getEquipmentId(), representative.getProcessId(), representative.getItemId(), KpiDataConstants.AGG_DAILY_BATCH, KpiDataConstants.BATCH_DAILY);


            KpiDataEntity batchKpi = existingBatchKpi.orElseGet(KpiDataEntity::new);

            batchKpi.setKpiDate(date);
            batchKpi.setEquipmentId(representative.getEquipmentId());
            batchKpi.setProcessId(representative.getProcessId());
            batchKpi.setItemId(representative.getItemId());
            batchKpi.setAggregationType(KpiDataConstants.AGG_DAILY_BATCH);
            batchKpi.setBatchCheck(KpiDataConstants.BATCH_DAILY);

            batchKpi.setStartTime(firstStartTime);
            batchKpi.setEndTime(lastEndTime);
            batchKpi.setCalcSuccessCheck(KpiDataConstants.CALC_SUCCESS);
            batchKpi.setCalcAt(LocalDateTime.now(ZoneOffset.UTC));
            batchKpi.setCreatedBy("system");

            batchKpi.setActualYield(kpiValues.get("yield"));
            batchKpi.setActualDefectRate(kpiValues.get("defectRate"));
            batchKpi.setActualOee(kpiValues.get("oee"));
            batchKpi.setActualProductivity(kpiValues.get("productivity"));

            kpiDataRepo.save(batchKpi);
        });
    }
}
