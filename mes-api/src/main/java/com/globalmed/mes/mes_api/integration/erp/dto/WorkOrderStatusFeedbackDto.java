package com.globalmed.mes.mes_api.integration.erp.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Work Order 상태 변경 시, ERP의 Plan 상태를 업데이트하기 위해 전송하는 DTO
 */
public record WorkOrderStatusFeedbackDto(
        String planId,              // 🚨 ERP의 ProductionPlan ID (필수 키)
        String workOrderId,
        String workOrderNumber,
        String newPlanStatus,       // 🚨 MES에서 산출한 ERP의 최종 Plan 상태 (e.g., RUNNING, FINISHED)
        String mesWoStatusCode,     // MES Work Order의 현재 상태 코드
        BigDecimal totalProducedQty, // Work Order의 누적 생산 수량 (진행률 반영용)
        OffsetDateTime updateTime
) {
    // Lombok이 없으므로 생성자 생략. 데이터 전송용으로만 사용합니다.
}