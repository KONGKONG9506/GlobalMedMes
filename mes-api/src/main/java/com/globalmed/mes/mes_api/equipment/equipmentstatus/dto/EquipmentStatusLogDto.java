package com.globalmed.mes.mes_api.equipment.equipmentstatus.dto;

import com.globalmed.mes.mes_api.equipment.equipmentstatus.domain.EquipmentStatusLogEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentStatusLogDto {
    private Long logId;
    private String equipmentId;
    private String statusCode;
    private String startTime;

    public EquipmentStatusLogDto(EquipmentStatusLogEntity entity) {
        this.logId = entity.getLogId();
        this.equipmentId = entity.getEquipment().getEquipmentId();
        this.statusCode = entity.getStatusCode().getCode(); // CommonCodeEntity의 code 값
        this.startTime = entity.getStartTime().toString();
    }
}
