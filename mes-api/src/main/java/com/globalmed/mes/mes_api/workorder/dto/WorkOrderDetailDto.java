package com.globalmed.mes.mes_api.workorder.dto;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record WorkOrderDetailDto(
        String workOrderId,
        String workOrderNumber,
        BigDecimal orderQty,
        BigDecimal producedQty,
        String statusCode,
        // 상태 코드 이름 추가
        OffsetDateTime startTs,
        OffsetDateTime createdAt,
        OffsetDateTime modifiedAt,
        // Item 관련 정보 추가
        String itemName,
        String itemCode,
        String itemType,
        String unit,
        String itemDescription,
        // Process 관련 정보 추가
        String processName,
        String processDescription,
        // Equipment 관련 정보 추가
        String equipmentName,
        String workcenterName  // UTC
        ) {public static WorkOrderDetailDto fromEntity(WorkOrderEntity entity) {
        // 엔티티에서 필요한 모든 정보를 가져와 DTO에 매핑
        String itemName = (entity.getItem() != null) ? entity.getItem().getItemName() : null;
        String itemCode = (entity.getItem() != null) ? entity.getItem().getItemCode() : null;
        String itemType = (entity.getItem() != null) ? entity.getItem().getItemType() : null;
        String unit = (entity.getItem() != null) ? entity.getItem().getUnit() : null;
        String itemDescription = (entity.getItem() != null) ? entity.getItem().getDescription() : null;
        String processName = (entity.getProcess() != null) ? entity.getProcess().getProcessName() : null;
        String processDescription = (entity.getProcess() != null) ? entity.getProcess().getDescription() : null;
        String equipmentName = (entity.getEquipment() != null) ? entity.getEquipment().getEquipmentName() : null;
        String workcenterName = (entity.getEquipment() != null && entity.getEquipment().getWorkcenter() != null)
                ? entity.getEquipment().getWorkcenter().getWorkcenterName() : null;
        String statusCode = (entity.getStatusCode() != null) ? entity.getStatusCode().getCode() : null;

        return new WorkOrderDetailDto(
                entity.getWorkOrderId(),
                entity.getWorkOrderNumber(),
                entity.getOrderQty(),
                entity.getProducedQty(),
                statusCode,
                entity.getStartTs() != null ? com.globalmed.mes.mes_api.common.DateTimeMapper.attachKst(entity.getStartTs()) : null,
                entity.getCreatedAt() != null ? com.globalmed.mes.mes_api.common.DateTimeMapper.attachKst(entity.getCreatedAt()) : null,
                entity.getModifiedAt() != null ? com.globalmed.mes.mes_api.common.DateTimeMapper.attachKst(entity.getModifiedAt()) : null,
                itemName, itemCode, itemType, unit, itemDescription,
                processName, processDescription,
                equipmentName, workcenterName
        );
        }
}