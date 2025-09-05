package com.globalmed.mes.mes_api.employee.shift.controller;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import com.globalmed.mes.mes_api.employee.shift.service.ShiftAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts/assignments")
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
}