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
public class KpiDataService {

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
     * OEE 계산 (임시, 런타임 계산식 변경 TODO)
     */
    public BigDecimal calculateOee(WorkOrderEntity wo, List<ProductionLogEntity> logs) {
        Map<String, BigDecimal> agg = aggregateProductionLogs(logs);
        // 실제 가동 시간 계산 (초 단위)
        BigDecimal runTime = BigDecimal.valueOf(Duration.between(wo.getStartTs(), wo.getEndTs()).toSeconds());

        // TODO: CMMS/인원관리 개발 후 런타임 계산식 변경 필요
        Map<String, Object> params = Map.of(
                "good_qty", agg.get("good_qty"),
                "total_qty", agg.get("total_qty"),
                "run_time", runTime,
                "planned_time", runTime // 현재는 임시로 run_time = planned_time
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
                "total_qty", agg.get("total_qty")
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
                "total_qty", agg.get("total_qty")
        );

        return definitionService.calculate("Defect Rate", params);
    }
}
