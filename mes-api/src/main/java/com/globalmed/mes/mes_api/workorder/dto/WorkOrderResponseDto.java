package com.globalmed.mes.mes_api.workorder.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WorkOrderResponseDto {
    private String workOrderId;
    private String workOrderNumber;
    private String itemId;
    private String processId;
    private String equipmentId;
    private BigDecimal orderQty;
    private BigDecimal producedQty;
    private String statusCode; // code 문자열만
}