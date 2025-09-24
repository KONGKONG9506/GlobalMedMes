package com.globalmed.mes.mes_api.equipstatus.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record EquipDetailDto(
        String equipmentId,
        String equipmentName,
        String workcenterName,
        String statusCode,
        OffsetDateTime createAt,
        String workOrederName,
        String itemName,
        Long produceQty,
        Long orderQty,
        List<EWorkerDto> workers,
        String shiftName,
        OffsetDateTime shiftStartTs,
        OffsetDateTime shiftEndTs,
        List<ECertDto> requiredCerts,
        List<EProcessDto> equipProcess,
        List<EProcessDto> userEProcess

) {
    public record EWorkerDto(
            String workerName,  // 작업자 이름
            String workerNumber // 작업자 사원 번호
    ) {}
    public record ECertDto(
            Long certId,    // 자격증 코드
            String certName,    // 자격증 이름
            String certDescription  //
    ) {}
    public record EProcessDto(
            Long processId,
            String processName,
            String processDescription
    ) {}
}
