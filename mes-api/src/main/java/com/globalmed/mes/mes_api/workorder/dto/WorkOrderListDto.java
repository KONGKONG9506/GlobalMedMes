package com.globalmed.mes.mes_api.workorder.dto;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import java.math.BigDecimal;


public record WorkOrderListDto(
        String workOrderId,
        String workOrderNumber,
        BigDecimal orderQty,
        BigDecimal producedQty,
        String statusCode,
        String itemName,
        String itemType,
        String unit,
        String processName,
        String equipmentName,
        String workcenterName
) {
    public static WorkOrderListDto fromEntity(WorkOrderEntity entity){
        String itemName = (entity.getItem() != null) ? entity.getItem().getItemName() : null;
        String itemType = (entity.getItem() != null) ? entity.getItem().getItemType() : null;
        String unit = (entity.getItem() != null) ? entity.getItem().getUnit() : null;
        String processName = (entity.getProcess() != null) ? entity.getProcess().getProcessName() : null;
        String equipmentName = (entity.getEquipment() != null) ? entity.getEquipment().getEquipmentName() : null;
        String workcenterName = (entity.getEquipment() != null && entity.getEquipment().getWorkcenter() != null)
                ? entity.getEquipment().getWorkcenter().getWorkcenterName() : null;

        return new WorkOrderListDto(
                entity.getWorkOrderId(),
                entity.getWorkOrderNumber(),
                entity.getOrderQty(),
                entity.getProducedQty(),
                entity.getStatusCode().getCode(),
                itemName,
                itemType,
                unit,
                processName,
                equipmentName,
                workcenterName
        );
    }
}