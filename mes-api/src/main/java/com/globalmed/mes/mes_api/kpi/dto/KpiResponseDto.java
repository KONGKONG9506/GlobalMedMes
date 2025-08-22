package com.globalmed.mes.mes_api.kpi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class KpiResponseDto {
    private Long targetId;
    private LocalDate kpiDate;
    private String equipmentId;
    private String processId;
    private String itemId;
    private BigDecimal targetOee;
    private BigDecimal targetYield;
    private BigDecimal targetProductivity;

    @Builder
    public KpiResponseDto(Long targetId, LocalDate kpiDate, String equipmentId, String processId,
                          String itemId, BigDecimal targetOee, BigDecimal targetYield, BigDecimal targetProductivity) {
        this.targetId = targetId;
        this.kpiDate = kpiDate;
        this.equipmentId = equipmentId;
        this.processId = processId;
        this.itemId = itemId;
        this.targetOee = targetOee;
        this.targetYield = targetYield;
        this.targetProductivity = targetProductivity;
    }
}
