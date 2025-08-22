package com.globalmed.mes.mes_api.OldCode.process.productionperformance.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductionPerformanceResponseDto {
    private Long performanceId;
    private String workOrderId;
    private String itemId;
    private String processId;
    private String equipmentId;
    private BigDecimal producedQty;
    private BigDecimal defectQty;
    private BigDecimal goodQty;  // = producedQty - defectQty
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String workerId;
}
