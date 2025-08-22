package com.globalmed.mes.mes_api.OldCode.kpi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class KpiRequestDto {
    private LocalDate kpiDate;
    private String equipmentId;
    private String processId;
    private String itemId;
    private BigDecimal targetOee;
    private BigDecimal targetYield;
    private BigDecimal targetProductivity;
}
