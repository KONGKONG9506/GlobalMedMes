package com.globalmed.mes.mes_api.OldCode.process.productionperformance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductionPerformanceListResponseDto {
    private Long performanceId;
    private String workOrderId;
    private String itemId;
    private String processId;
    private String equipmentId;
    private BigDecimal producedQty;
    private BigDecimal defectQty;
}
