package com.globalmed.mes.mes_api.employee.shift.controller;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftAssignmentDto;
import com.globalmed.mes.mes_api.employee.shift.service.ShiftAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/shifts")
@RequiredArgsConstructor
public class ShiftAssignmentController {
    private final ShiftAssignmentService assignmentService;

    @PostMapping("/{calendarId}/assign")
    public ResponseEntity<ShiftAssignmentEntity> assignWorker(
            @PathVariable Long calendarId,
            @RequestParam String workerId
    ) {
        ShiftAssignmentEntity assignment = assignmentService.assignWorker(calendarId, workerId);
        return ResponseEntity.ok(assignment);
    }
    @GetMapping("/assignments")
    public ResponseEntity<List<ShiftAssignmentDto>> getAssignmentsByPeriod(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        List<ShiftAssignmentDto> assignments = assignmentService.getAssignmentsByDate(startDate, endDate);
        return ResponseEntity.ok(assignments);
    }
}