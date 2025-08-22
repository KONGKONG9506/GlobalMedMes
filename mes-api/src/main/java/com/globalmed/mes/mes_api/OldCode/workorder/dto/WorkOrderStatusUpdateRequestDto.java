package com.globalmed.mes.mes_api.OldCode.workorder.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderStatusUpdateRequestDto {
    private String toStatus; // "R" 또는 "C" 같은 상태코드
}