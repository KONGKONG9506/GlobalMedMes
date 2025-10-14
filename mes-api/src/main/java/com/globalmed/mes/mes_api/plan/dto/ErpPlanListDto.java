package com.globalmed.mes.mes_api.plan.dto;


import com.globalmed.mes.mes_api.plan.domain.TbProductionPlan;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * React FE에 목록 형태로 보여줄 간략 정보 DTO
 */
public record ErpPlanListDto(
        String planId,
        String planNumber, // ERP의 planCode
        String itemId,
        LocalDate startDate,
        BigDecimal targetQty,
        String status,     // MES Plan 상태 (P, R, C 등)
        long workOrderCount // 연결된 Work Order 수
) {
    public static ErpPlanListDto fromPlanEntity(TbProductionPlan plan, long woCount) {
        return new ErpPlanListDto(
                plan.getPlanId(),
                plan.getPlanNumber(),
                plan.getItemId(),
                plan.getStartDate(),
                plan.getTargetQty(),
                plan.getStatus(),
                woCount
        );
    }
}