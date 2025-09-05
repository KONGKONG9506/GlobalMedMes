package com.globalmed.mes.mes_api.employee.shift.service;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftCalendarEntity;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftAssignmentRepo;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftCalendarRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShiftAssignmentService {
    private final ShiftAssignmentRepo assignmentRepo;
    private final ShiftCalendarRepo calendarRepo;
    @Transactional
    public ShiftAssignmentEntity assignWorker(Long calendarId, String workerId) {
        // 1. 교대 달력 조회
        ShiftCalendarEntity calendar = calendarRepo.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // 2. 중복 배치 확인
        boolean exists = assignmentRepo.existsAssignment(
                calendar.getShiftDate(),
                calendar.getShift().getShiftId(),
                workerId,
                calendar.getEquipmentId(),
                calendar.getWorkcenterId()
        );
        if (exists) {
            throw new IllegalStateException("DUPLICATE_KEY");
        }

        // 3. 배치 생성
        ShiftAssignmentEntity assignment = new ShiftAssignmentEntity();
        assignment.setShiftDate(calendar.getShiftDate());
        assignment.setShift(calendar.getShift());
        assignment.setWorkerId(workerId);
        assignment.setEquipmentId(calendar.getEquipmentId());
        assignment.setWorkcenterId(calendar.getWorkcenterId());
        assignment.setStartTs(calendar.getStartTs());
        assignment.setEndTs(calendar.getEndTs());

        return assignmentRepo.save(assignment);
    }
}
