package com.globalmed.mes.mes_api.equipstatus.controller;

import com.globalmed.mes.mes_api.code.domain.ItemEntity;
import com.globalmed.mes.mes_api.code.repository.ItemRepo;
import com.globalmed.mes.mes_api.code.repository.ProcessRepo;
import com.globalmed.mes.mes_api.common.PageResponse;
import com.globalmed.mes.mes_api.employee.cert.repository.CertRepo;
import com.globalmed.mes.mes_api.employee.cert.repository.EquipmentCertRepo;
import com.globalmed.mes.mes_api.employee.cert.repository.ProcessCertRepo;
import com.globalmed.mes.mes_api.employee.domain.EmployeeEntity;
import com.globalmed.mes.mes_api.employee.repository.EmployeeCertRepo;
import com.globalmed.mes.mes_api.employee.repository.EmployeeRepo;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftAssignmentRepo;
import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipstatus.dto.EquipDetailDto;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/equip")
@RequiredArgsConstructor
public class EquipmentDetailController {
    private final EquipmentRepo equipmentRepo;
    private final WorkOrderRepo workOrderRepo;
    private final ShiftAssignmentRepo shiftAssignRepo;
    private final ItemRepo itemRepo;
    private final CertRepo certRepo;
    private final EquipmentCertRepo equipCertRepo;
    private final ProcessRepo processRepo;
    private final ProcessCertRepo proCertRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeCertRepo employeeCertRepo;

    @GetMapping
    public ResponseEntity<PageResponse<EquipDetailDto>> list(
            @RequestParam String equipmentId) {

        EquipmentEntity equipment = equipmentRepo.findDetail(equipmentId);

        Optional<WorkOrderEntity> lastWorkOredr = workOrderRepo.findFirstByEquipmentId_EquipmentIdOrderByCreatedAtDesc(equipmentId);
        WorkOrderEntity workOrder = lastWorkOredr.filter(wo -> "R".equals(wo.getStatusCode().getCode()))
                .orElse(null);




        LocalDateTime localNow = LocalDateTime.now();
        OffsetDateTime now = localNow
                .atZone(ZoneId.systemDefault()) // 서버 시간대 기준
                .toOffsetDateTime()             // OffsetDateTime으로 변환
                .withOffsetSameInstant(ZoneOffset.UTC); // UTC 기준;
        Optional<ShiftAssignmentEntity> shiftAssign = shiftAssignRepo.findEquipShiftNow(equipmentId, now);
        String shiftName = shiftAssign.map(sa -> sa.getShift().getShiftName()).orElse(null);
        OffsetDateTime shiftStartTs = shiftAssign.map(ShiftAssignmentEntity::getStartTs).orElse(null);
        OffsetDateTime shiftEndTs = shiftAssign.map(ShiftAssignmentEntity::getEndTs).orElse(null);

        List<EquipDetailDto.EWorkerDto> workers = shiftAssign.stream()
                .map(sa -> employeeRepo.findById(sa.getWorkerId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(e -> new EquipDetailDto.EWorkerDto(e.getEmployeeName(), e.getEmployeeNumber()))
                .toList();

        equipCert = equipCertRepo.findEquiprequier(equipmentId);
        proCert = proCertRepo.findProCert(equipment.getProcess().getProcessId());
        employeeCert = employeeCertRepo.findEmployeeCert();

        detaile = new EquipDetailDto(
                equipmentId,
                equipment.getEquipmentName(),
                equipment.getWorkcenter(),
                equipment.getStatusCode(),
                equipment.getCreatedAt(),
                workOrder.getWorkOrderNumber(),
                workOrder.getItemId().getItemName(),
                workOrder.getProducedQty(),
                workOrder.getOrderQty(),
                workers,
                shiftName,
                shiftStartTs,
                shiftEndTs,
                equipCert,
                equipPro,
                userEqPro
        )


        return null;
    }
}
