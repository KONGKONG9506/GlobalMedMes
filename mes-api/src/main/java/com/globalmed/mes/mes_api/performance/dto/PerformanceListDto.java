package com.globalmed.mes.mes_api.performance.dto;

import com.globalmed.mes.mes_api.performance.domain.ProductionPerformanceEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PerformanceListDto(
        String workOrderId,
        String itemId,
        String processId,
        String equipmentId,
        BigDecimal producedQty,
        BigDecimal defectQty,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String workerId,
        String createdBy
) {
    public static PerformanceListDto fromEntity(ProductionPerformanceEntity e) {
        return new PerformanceListDto(
                e.getWorkOrderId(),
                e.getItemId(),
                e.getProcessId(),
                e.getEquipmentId(),
                e.getProducedQty(),
                e.getDefectQty(),
                e.getStartTime(),
                e.getEndTime(),
                e.getWorkerId(),
                e.getCreatedBy()
        );
    }
}