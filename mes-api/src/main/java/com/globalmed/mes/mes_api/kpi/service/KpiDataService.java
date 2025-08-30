package com.globalmed.mes.mes_api.kpi.service;

import com.globalmed.mes.mes_api.kpi.KpiDataConstants;
import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import com.globalmed.mes.mes_api.kpi.repository.KpiDataRepo;
import com.globalmed.mes.mes_api.performance.domain.ProductionPerformanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiDataService {

    private final KpiCalculationService kpiCalculationService;
    private final KpiDataRepo kpiDataRepo;

    /**
     * ProductionPerformanceEntity 기반 KPI 실시간 계산 및 저장/갱신
     */
    @Transactional
    public void saveKpiFromPerformance(List<ProductionPerformanceEntity> performances) {
        LocalDate kpiDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (ProductionPerformanceEntity p : performances) {
            // KPI 계산
            BigDecimal goodQty = p.getProducedQty().subtract(p.getDefectQty());
            BigDecimal defectQty = p.getDefectQty();
            BigDecimal totalQty = goodQty.add(defectQty);

            BigDecimal yield = kpiCalculationService.calculateYieldFromValues(goodQty, totalQty);
            BigDecimal defectRate = kpiCalculationService.calculateDefectRateFromValues(defectQty, totalQty);
            BigDecimal runSeconds = BigDecimal.valueOf(java.time.Duration.between(p.getStartTime(), p.getEndTime()).toSeconds());
            BigDecimal oee = kpiCalculationService.calculateOeeFromValues(goodQty, totalQty, runSeconds, runSeconds);

            // 실시간 KPI는 performance_id 기준 UNIQUE
            KpiDataEntity kpi = kpiDataRepo.findByPerformanceIdAndKpiDate(p.getPerformanceId(), kpiDate)
                    .orElseGet(KpiDataEntity::new);

            kpi.setKpiDate(kpiDate);
            kpi.setEquipmentId(p.getEquipmentId());
            kpi.setProcessId(p.getProcessId());
            kpi.setItemId(p.getItemId());
            kpi.setPerformanceId(p.getPerformanceId());
            kpi.setAggregationType(KpiDataConstants.AGG_REALTIME);

            kpi.setActualYield(yield);
            kpi.setActualDefectRate(defectRate);
            kpi.setActualOee(oee);
            kpi.setActualProductivity(BigDecimal.ZERO); // 필요하면 계산 로직 추가

            kpi.setStartTime(p.getStartTime());
            kpi.setEndTime(p.getEndTime());
            kpi.setCalcStatus(KpiDataConstants.CALC_SUCCESS);
            kpi.setCalcAt(now);
            kpi.setCreatedBy("system");

            kpiDataRepo.save(kpi);
        }
    }

    /**
     * 주기적으로(일일) KPI 저장 (performance_id 없이)
     */
    @Transactional
    public void saveBatchKpi(KpiDataEntity batchKpi) {
        // 배치 KPI의 aggregationType을 DAILY_BATCH로 강제
        batchKpi.setAggregationType("DAILY_BATCH");

        // UNIQUE: kpi_date + equipment + process + item + aggregation_type
        KpiDataEntity kpi = kpiDataRepo.findByKpiDateAndEquipmentIdAndProcessIdAndItemIdAndAggregationType(
                batchKpi.getKpiDate(),
                batchKpi.getEquipmentId(),
                batchKpi.getProcessId(),
                batchKpi.getItemId(),
                batchKpi.getAggregationType()
        ).orElse(batchKpi);

        kpi.setActualOee(batchKpi.getActualOee());
        kpi.setActualProductivity(batchKpi.getActualProductivity());
        kpi.setActualYield(batchKpi.getActualYield());
        kpi.setActualDefectRate(batchKpi.getActualDefectRate());
        kpi.setStartTime(batchKpi.getStartTime());
        kpi.setEndTime(batchKpi.getEndTime());
        kpi.setCalcStatus(KpiDataConstants.CALC_SUCCESS);
        kpi.setCalcAt(LocalDateTime.now());
        kpi.setCreatedBy(batchKpi.getCreatedBy());

        kpiDataRepo.save(kpi);
    }
}
