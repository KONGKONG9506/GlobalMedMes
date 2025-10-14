package com.globalmed.mes.mes_api.employee.shift.service;

import com.globalmed.mes.mes_api.employee.cert.service.EquipmentCertCheckService;
import com.globalmed.mes.mes_api.employee.domain.EmployeeEntity;
import com.globalmed.mes.mes_api.employee.repository.EmployeeRepo;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftCalendarEntity;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftAssignmentDto;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftAssignmentViewDto;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftDto;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftAssignmentRepo;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftCalendarRepo;
import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftAssignmentService {
    private final ShiftAssignmentRepo assignmentRepo;
    private final ShiftCalendarRepo calendarRepo;
    private final EmployeeRepo employeeRepo;
    private final EquipmentRepo equipmentRepo;
    private final EquipmentCertCheckService equipmentCertCheckService;

    @Transactional
    public ShiftAssignmentEntity assignWorker(Long calendarId, String workerId) {
        // 1. 교대 달력 조회
        ShiftCalendarEntity calendar = calendarRepo.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        boolean employeeExists = employeeRepo.existsById(workerId);
        if (!employeeExists) {
            throw new IllegalArgumentException("WORKER_NOT_FOUND");
        }
        equipmentCertCheckService.check(workerId, calendar.getEquipmentId());

        // 2. 중복 배치 확인
        boolean exists = assignmentRepo.existsAssignment(
                calendar.getShiftDate(),
                calendar.getShift().getShiftId(),
                workerId,
                calendar.getEquipmentId(),
                calendar.getWorkcenterId()
        );
        if (exists) {
            throw new IllegalStateException("DUPLICATE_EMPLOYEE");
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

    public List<ShiftAssignmentDto> getAssignmentsByDate(LocalDate startDate, LocalDate endDate, String equipmentId, String workcenterId) {

        List<ShiftAssignmentEntity> assignments = assignmentRepo.findByShiftDateBetween(startDate, endDate)
                .stream()
                .filter(a -> equipmentId == null || equipmentId.equals(a.getEquipmentId()))
                .filter(a -> workcenterId == null || workcenterId.equals(a.getWorkcenterId()))
                .toList();
        List<ShiftAssignmentDto> dtos = new ArrayList<>();

        for (ShiftAssignmentEntity a : assignments) {

            EquipmentEntity eq = equipmentRepo.findById(a.getEquipmentId())
                    .orElseThrow(() -> new IllegalStateException("Equipment not found: " + a.getEquipmentId()));

            ShiftDto shiftDto = new ShiftDto(
                    a.getShift().getShiftId(),
                    a.getShift().getShiftCode(),
                    a.getShift().getShiftName(),
                    a.getShift().getStartTime().toString(),
                    a.getShift().getEndTime().toString()
            );
            dtos.add(new ShiftAssignmentDto(
                    a.getShiftDate(),
                    shiftDto,
                    a.getWorkerId(),
                    a.getEquipmentId(),
                    eq.getEquipmentName(),
                    a.getWorkcenterId(),
                    eq.getWorkcenter().getWorkcenterName(),
                    a.getStartTs(),
                    a.getEndTs()
            ));
        }

        return dtos;
    }
    public List<ShiftAssignmentViewDto> getAssignmentViews(LocalDate startDate, LocalDate endDate,
                                                           String equipmentId, String workcenterId) {
        List<ShiftAssignmentEntity> assignments = assignmentRepo.findByShiftDateBetween(startDate, endDate)
                .stream()
                .filter(a -> equipmentId == null || equipmentId.equals(a.getEquipmentId()))
                .filter(a -> workcenterId == null || workcenterId.equals(a.getWorkcenterId()))
                .toList();

        // 날짜+교대+설비/워크센터 단위로 그룹핑
        Map<String, List<ShiftAssignmentEntity>> grouped = assignments.stream()
                .collect(Collectors.groupingBy(a ->
                        a.getShiftDate() + "_" +
                                a.getShift().getShiftName() + "_" +
                                (a.getEquipmentId() != null ? a.getEquipmentId() : a.getWorkcenterId())
                ));


        return grouped.values().stream()
                .map(list -> {
                    ShiftAssignmentEntity first = list.get(0);

                    List<String> workerNames = list.stream()
                            .map(a -> employeeRepo.findById(a.getWorkerId())
                                    .map(EmployeeEntity::getEmployeeName)
                                    .orElse("이름없음"))
                            .toList();

                    String firstWorkerName = workerNames.isEmpty() ? "없음" : workerNames.get(0);

                    EquipmentEntity eq = equipmentRepo.findById(first.getEquipmentId())
                            .orElseThrow(() -> new IllegalStateException("Equipment not found: " + first.getEquipmentId()));

                    int workerCount = list.size();
                    String workerDisplay = (workerCount > 1)
                            ? firstWorkerName + " 외 " + (workerCount - 1) + "명"
                            : firstWorkerName;

                    LocalDateTime start = first.getStartTs()
                            .atZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();
                    LocalDateTime end = first.getEndTs()
                            .atZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();

                    return new ShiftAssignmentViewDto(
                            start,
                            end,
                            first.getShift().getShiftName(),
                            first.getEquipmentId(),
                            eq.getEquipmentName(),
                            first.getWorkcenterId(),
                            eq.getWorkcenter().getWorkcenterName(),
                            workerDisplay,
                            workerNames,
                            workerCount
                    );
                })
                .toList();
    }

}
