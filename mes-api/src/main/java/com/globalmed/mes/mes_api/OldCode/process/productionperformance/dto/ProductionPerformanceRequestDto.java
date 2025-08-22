package com.globalmed.mes.mes_api.OldCode.process.productionperformance.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductionPerformanceRequestDto {
    private String workOrderId;
    private String itemId;
    private String processId;
    private String equipmentId;
    private BigDecimal producedQty;
    private BigDecimal defectQty;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
