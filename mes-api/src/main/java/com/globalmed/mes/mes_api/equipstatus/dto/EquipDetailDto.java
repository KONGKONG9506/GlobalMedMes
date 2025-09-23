package com.globalmed.mes.mes_api.equipstatus.dto;

import java.math.BigDecimal;
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
        BigDecimal produceQty,
        BigDecimal orderQty,
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
            String certCode,    // 자격증 코드
            String certName,    // 자격증 이름
            String certDescription  // 자격증 설명
    ) {}
    public record EProcessDto(
            String processId,
            String processName,
            String processDescription
    ) {}
}
