package com.globalmed.mes.mes_api.OldCode.workorder.dto;

import com.globalmed.mes.mes_api.OldCode.workorder.domain.WorkOrderEntity;
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
    public static WorkOrderResponseDto fromEntity(WorkOrderEntity entity) {
        WorkOrderResponseDto dto = new WorkOrderResponseDto();
        dto.workOrderId = entity.getWorkOrderId();
        dto.workOrderNumber = entity.getWorkOrderNumber();
        dto.itemId = entity.getItemId();
        dto.processId = entity.getProcessId();
        dto.equipmentId = entity.getEquipmentId();
        dto.orderQty = entity.getOrderQty();
        dto.producedQty = entity.getProducedQty();
        dto.statusCode = entity.getStatusCode().getCode();
        return dto;
    }
}