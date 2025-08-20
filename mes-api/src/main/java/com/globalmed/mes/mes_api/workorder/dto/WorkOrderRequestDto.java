package com.globalmed.mes.mes_api.workorder.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WorkOrderRequestDto {
    private String workOrderNumber;
    private String itemId;
    private String processId;
    private String equipmentId;
    private BigDecimal orderQty;
}
