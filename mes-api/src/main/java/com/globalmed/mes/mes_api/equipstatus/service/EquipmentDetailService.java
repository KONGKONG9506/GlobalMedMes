package com.globalmed.mes.mes_api.equipstatus.service;

import com.globalmed.mes.mes_api.code.dto.CertDto;
import com.globalmed.mes.mes_api.code.dto.ProcessDto;
import com.globalmed.mes.mes_api.process.domain.ProcessEntity;
import com.globalmed.mes.mes_api.employee.cert.domain.CertEntity;
import com.globalmed.mes.mes_api.employee.cert.domain.EquipmentCertEntity;
import com.globalmed.mes.mes_api.employee.cert.domain.ProcessCertEntity;
import com.globalmed.mes.mes_api.employee.cert.repository.EquipmentCertRepo;
import com.globalmed.mes.mes_api.employee.cert.repository.ProcessCertRepo;
import com.globalmed.mes.mes_api.employee.domain.EmployeeCertEntity;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentDetailService {
    private final EquipmentRepo equipmentRepo;
    private final WorkOrderRepo workOrderRepo;
    private final ShiftAssignmentRepo shiftAssignRepo;
    private final EquipmentCertRepo equipCertRepo;
    private final ProcessCertRepo proCertRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeCertRepo employeeCertRepo;

    @Transactional(readOnly = true)
    public EquipDetailDto getEquipmentDetail(String equipmentId) {
        EquipmentEntity equipment = equipmentRepo.findDetail(equipmentId);
        if (equipment == null) {
            return null;
        }

        Optional<WorkOrderEntity> lastWorkOrder = workOrderRepo.findFirstByEquipmentId_EquipmentIdOrderByCreatedAtDesc(equipmentId);
        WorkOrderEntity workOrder = lastWorkOrder.filter(wo -> "R".equals(wo.getStatusCode().getCode()))
                .orElse(null);

        LocalDateTime localNow = LocalDateTime.now();
        OffsetDateTime now = localNow
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime()
                .withOffsetSameInstant(ZoneOffset.UTC);

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

        List<EquipmentCertEntity> equipCerts = equipCertRepo.findEquiprequier(equipmentId);
        List<CertEntity> requiredCerts = equipCerts.stream()
                .map(EquipmentCertEntity::getCert)
                .toList();
        List<CertDto> equipCertDtos = requiredCerts.stream()
                .map(c -> new CertDto(
                        c.getCertCode(),
                        c.getCertName(),
                        c.getCertDescription()
                ))
                .toList();

        List<ProcessDto> equipPro = List.of(
                new ProcessDto(
                        equipment.getProcess().getProcessId(),
                        equipment.getProcess().getProcessName(),
                        equipment.getProcess().getDescription()
                )
        );

        List<ProcessCertEntity> proCert = proCertRepo.findProCert(equipment.getProcess().getProcessId());
        List<EmployeeCertEntity> employeeCerts = employeeCertRepo.findEmployeeCerts(shiftAssign.stream()
                .map(ShiftAssignmentEntity::getWorkerId)
                .toList());

        Set<Long> employeeCertIds = employeeCerts.stream()
                .map(ec -> ec.getCert().getCertId())
                .collect(Collectors.toSet());

        Map<String, Set<Long>> processRequiredCertIds = proCert.stream()
                .collect(Collectors.groupingBy(
                        pc -> pc.getProcess().getProcessId(),
                        Collectors.mapping(pc -> pc.getCert().getCertId(), Collectors.toSet())
                ));

        List<ProcessDto> userEqPro = processRequiredCertIds.entrySet().stream()
                .filter(entry -> employeeCertIds.containsAll(entry.getValue()))
                .map(entry -> {
                    ProcessEntity process = proCert.stream()
                            .filter(pc -> pc.getProcess().getProcessId().equals(entry.getKey()))
                            .findFirst()
                            .get()
                            .getProcess();
                    return new ProcessDto(
                            process.getProcessId(),
                            process.getProcessName(),
                            process.getDescription()
                    );
                })
                .toList();

        return new EquipDetailDto(
                equipmentId,
                equipment.getEquipmentName(),
                equipment.getWorkcenter().getWorkcenterId(),
                equipment.getStatusCode().getName(),
                equipment.getCreatedAt().atOffset(ZoneOffset.UTC),
                workOrder != null ? workOrder.getWorkOrderNumber() : null,
                workOrder != null ? workOrder.getItemId().getItemName() : null,
                workOrder != null ? workOrder.getProducedQty() : null,
                workOrder != null ? workOrder.getOrderQty() : null,
                workers,
                shiftName,
                shiftStartTs,
                shiftEndTs,
                equipCertDtos,
                equipPro,
                userEqPro
        );
    }
}
