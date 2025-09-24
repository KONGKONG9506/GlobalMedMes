package com.globalmed.mes.mes_api.equipstatus.dto;

import com.globalmed.mes.mes_api.code.dto.CertDto;
import com.globalmed.mes.mes_api.code.dto.ProcessDto;

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
        List<CertDto> requiredCerts,
        List<ProcessDto> equipProcess

) {
    public record EWorkerDto(
            String workerName,  // 작업자 이름
            String workerNumber // 작업자 사원 번호
    ) {}
}
