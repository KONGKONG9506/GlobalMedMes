package com.globalmed.mes.mes_api.equipment.equipmentstatus.dto;

public class EquipmentStatusRequestDto {
    private String equipmentId;
    private String statusCode;   // 예: RUN, STOP, IDLE 등

    // getter / setter
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
}
