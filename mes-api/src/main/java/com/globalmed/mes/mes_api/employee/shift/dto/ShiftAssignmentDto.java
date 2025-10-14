package com.globalmed.mes.mes_api.employee.shift.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentDto {
    private LocalDate shiftDate;
    private ShiftDto shift;
    private String workerId;
    private String equipmentId;
    private String equipmentName;
    private String workcenterId;
    private String workcenterName;
    private OffsetDateTime startTs;
    private OffsetDateTime endTs;
}
