package com.globalmed.mes.mes_api.employee.shift.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShiftCalendarDto(
        Long calendarId,
        LocalDate shiftDate,
        String shiftName,
        String equipmentId,
        String equipmentName,
        String workcenterId,
        String workcenterName,
        LocalDateTime startTs,
        LocalDateTime endTs
) {}
