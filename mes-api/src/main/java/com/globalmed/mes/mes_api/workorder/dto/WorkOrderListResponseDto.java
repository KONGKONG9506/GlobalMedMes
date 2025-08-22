package com.globalmed.mes.mes_api.workorder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class WorkOrderListResponseDto {
    private String workOrderId;
    private String workOrderNumber;
    private String statusCode;
    private String statusName;
    private String itemId;
    private String equipmentId;
    private String processId;
}
