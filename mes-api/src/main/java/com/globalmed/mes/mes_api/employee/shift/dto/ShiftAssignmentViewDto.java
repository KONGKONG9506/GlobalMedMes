package com.globalmed.mes.mes_api.employee.shift.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShiftAssignmentViewDto(
        LocalDateTime startTs,
        LocalDateTime endTs,
        String shiftName,
        String equipmentId,
        String equipmentName,
        String workcenterId,
        String workcenterName,
        String workerDisplay,
        List<String> workerNames,
        int workerCount
) {
}
