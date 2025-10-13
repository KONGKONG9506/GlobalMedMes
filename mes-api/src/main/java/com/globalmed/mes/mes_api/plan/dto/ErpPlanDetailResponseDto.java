package com.globalmed.mes.mes_api.plan.dto;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderDetailDto;
import com.globalmed.mes.mes_api.plan.domain.TbProductionPlan;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * MES 프론트엔드에 ERP에서 수신된 Plan과 Work Order 목록을 함께 제공하는 응답 DTO
 */
public record ErpPlanDetailResponseDto(
        String planId,
        String planNumber, // ERP의 planCode
        String itemId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal targetQty,
        String status, // MES Plan 상태 (P, R, C 등)

        // 🚨 연결된 Work Order 목록
        List<WorkOrderDetailDto> workOrders
) {
    public static ErpPlanDetailResponseDto fromPlanEntity(TbProductionPlan plan, List<WorkOrderEntity> woEntities) {

        List<WorkOrderDetailDto> woDetails = woEntities.stream()
                .map(WorkOrderDetailDto::fromEntity)
                .toList();

        return new ErpPlanDetailResponseDto(
                plan.getPlanId(),
                plan.getPlanNumber(),
                plan.getItemId(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getTargetQty(),
                plan.getStatus(),
                woDetails
        );
    }
}