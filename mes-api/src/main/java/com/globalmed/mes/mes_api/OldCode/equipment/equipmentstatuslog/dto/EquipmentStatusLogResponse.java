package com.globalmed.mes.mes_api.OldCode.equipment.equipmentstatuslog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class EquipmentStatusLogResponse {

    private Long logId;
    private String equipmentId;
    private String equipmentName;
    private String statusCode;
    private String statusName;
    private String reasonCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String createdBy;
    private LocalDateTime createdAt;
}
