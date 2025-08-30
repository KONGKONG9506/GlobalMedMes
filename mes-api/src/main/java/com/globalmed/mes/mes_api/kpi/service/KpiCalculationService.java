package com.globalmed.mes.mes_api.kpi.service;

import com.globalmed.mes.mes_api.code.Definition.service.DefinitionService;
import com.globalmed.mes.mes_api.production.domain.ProductionLogEntity;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KpiCalculationService {

    private final DefinitionService definitionService;

    /**
     * ProductionLog 기반 양품/불량/총 생산 수 집계
     */
    private Map<String, BigDecimal> aggregateProductionLogs(List<ProductionLogEntity> logs) {
        BigDecimal goodQty = logs.stream()
                .filter(l -> "GOODQTY".equals(l.getEventType().getCode()))
                .map(ProductionLogEntity::getEventValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal defectQty = logs.stream()
                .filter(l -> "DEFECTQTY".equals(l.getEventType().getCode()))
                .map(ProductionLogEntity::getEventValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "good_qty", goodQty,
                "defect_qty", defectQty,
                "total_qty", goodQty.add(defectQty)
        );
    }

    /**
     * OEE 계산
     */
    public BigDecimal calculateOee(WorkOrderEntity wo, List<ProductionLogEntity> logs) {
        Map<String, BigDecimal> agg = aggregateProductionLogs(logs);
        BigDecimal runTime = BigDecimal.valueOf(Duration.between(wo.getStartTs(), wo.getEndTs()).toSeconds());

        Map<String, Object> params = Map.of(
                "good_qty", agg.get("good_qty"),
                "total_qty", agg.get("total_qty"),
                "run_time", runTime,
                "planned_time", runTime,  // 임시
                "hundred", BigDecimal.valueOf(100) // 필수 상수
        );

        return definitionService.calculate("OEE", params);
    }

    /**
     * 수율(Yield) 계산
     */
    public BigDecimal calculateYield(List<ProductionLogEntity> logs) {
        Map<String, BigDecimal> agg = aggregateProductionLogs(logs);

        Map<String, Object> params = Map.of(
                "good_qty", agg.get("good_qty"),
                "total_qty", agg.get("total_qty"),
                "hundred", BigDecimal.valueOf(100)
        );

        return definitionService.calculate("Yield", params);
    }

    /**
     * 불량률(Defect Rate) 계산
     */
    public BigDecimal calculateDefectRate(List<ProductionLogEntity> logs) {
        Map<String, BigDecimal> agg = aggregateProductionLogs(logs);

        Map<String, Object> params = Map.of(
                "defect_qty", agg.get("defect_qty"),
                "total_qty", agg.get("total_qty"),
                "hundred", BigDecimal.valueOf(100)
        );

        return definitionService.calculate("Defect Rate", params);
    }

    /**
     * 생산성 계산
     */
    public BigDecimal calculateProductivity(BigDecimal producedQty, BigDecimal runSeconds) {
        Map<String, Object> params = Map.of(
                "produced_qty", producedQty,
                "run_time", runSeconds
        );

        return definitionService.calculate("Productivity", params);
    }

    /**
     * 임시 메서드: 직접 값으로 계산
     */
    public BigDecimal calculateOeeFromValues(BigDecimal goodQty, BigDecimal totalQty, BigDecimal runSeconds, BigDecimal plannedSeconds) {
        Map<String, Object> params = Map.of(
                "good_qty", goodQty,
                "total_qty", totalQty,
                "run_time", runSeconds,
                "planned_time", plannedSeconds,
                "hundred", BigDecimal.valueOf(100)
        );

        return definitionService.calculate("OEE", params);
    }

    public BigDecimal calculateYieldFromValues(BigDecimal goodQty, BigDecimal totalQty) {
        Map<String, Object> params = Map.of(
                "good_qty", goodQty,
                "total_qty", totalQty,
                "hundred", BigDecimal.valueOf(100)
        );

        return definitionService.calculate("Yield", params);
    }

    public BigDecimal calculateDefectRateFromValues(BigDecimal defectQty, BigDecimal totalQty) {
        Map<String, Object> params = Map.of(
                "defect_qty", defectQty,
                "total_qty", totalQty,
                "hundred", BigDecimal.valueOf(100)
        );

        return definitionService.calculate("Defect Rate", params);
    }
}
