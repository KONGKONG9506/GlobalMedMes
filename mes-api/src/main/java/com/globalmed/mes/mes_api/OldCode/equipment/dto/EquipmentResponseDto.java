package com.globalmed.mes.mes_api.OldCode.equipment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentResponseDto {
    private String equipmentId;
    private String equipmentName;
    private String statusCode; // 코드 값만
    private String statusName; // 코드 이름
}
