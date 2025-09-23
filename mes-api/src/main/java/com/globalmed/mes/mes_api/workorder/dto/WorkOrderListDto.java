package com.globalmed.mes.mes_api.workorder.dto;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;



public record WorkOrderListDto(
        String workOrderId,
        String workOrderNumber,
        BigDecimal orderQty,
        BigDecimal producedQty,
<<<<<<< HEAD
        String status,
=======
        String statusCode,
>>>>>>> origin/범수
        OffsetDateTime startTs,
        OffsetDateTime createdAt,
        OffsetDateTime modifiedAt,
        String itemName,
        String itemId,
        String itemType,
        String unit,
        String itemDescription,
        String processId,
<<<<<<< HEAD
=======
        String processName,
>>>>>>> origin/범수
        String processDescription,
        String equipmentId,
        String equipmentName,
        String workcenterName
) {
    public static WorkOrderListDto fromEntity(WorkOrderEntity entity){
        String itemName = (entity.getItemId() != null) ? entity.getItemId().getItemName() : null;
        String itemId = (entity.getItemId() != null) ? entity.getItemId().getItemCode() : null;
        String itemType = (entity.getItemId() != null) ? entity.getItemId().getItemType() : null;
        String unit = (entity.getItemId() != null) ? entity.getItemId().getUnit() : null;
        String itemDescription = (entity.getItemId() != null) ? entity.getItemId().getDescription() : null;
<<<<<<< HEAD
        String processId = (entity.getProcessId() != null) ? entity.getProcessId().getProcessName() : null;
=======
        String processId = (entity.getProcessId() != null) ? entity.getProcessId().getProcessId() : null;
        String processName = (entity.getProcessId() != null) ? entity.getProcessId().getProcessName() : null;
>>>>>>> origin/범수
        String processDescription = (entity.getProcessId() != null) ? entity.getProcessId().getDescription() : null;
        String equipmentId = (entity.getEquipmentId() != null) ? entity.getEquipmentId().getEquipmentId() : null;
        String equipmentName = (entity.getEquipmentId() != null) ? entity.getEquipmentId().getEquipmentName() : null;
        String workcenterName = (entity.getEquipmentId() != null && entity.getEquipmentId().getWorkcenter() != null)
                ? entity.getEquipmentId().getWorkcenter().getWorkcenterName() : null;
        String statusCode = (entity.getStatusCode() != null) ? entity.getStatusCode().getCode() : null;


        return new WorkOrderListDto(
                entity.getWorkOrderId(),
                entity.getWorkOrderNumber(),
                entity.getOrderQty(),
                entity.getProducedQty(),
                statusCode,
                entity.getStartTs() != null ? com.globalmed.mes.mes_api.common.DateTimeMapper.attachKst(entity.getStartTs()) : null,
                entity.getCreatedAt() != null ? com.globalmed.mes.mes_api.common.DateTimeMapper.attachKst(entity.getCreatedAt()) : null,
                entity.getModifiedAt() != null ? com.globalmed.mes.mes_api.common.DateTimeMapper.attachKst(entity.getModifiedAt()) : null,
<<<<<<< HEAD
                itemName, itemType, unit, itemDescription, processDescription,equipmentName, workcenterName,
                processId,itemId,equipmentId
=======
                itemName, itemId ,itemType, unit, itemDescription,processId,processName,processDescription,equipmentId,equipmentName, workcenterName

>>>>>>> origin/범수
        );
    }
}