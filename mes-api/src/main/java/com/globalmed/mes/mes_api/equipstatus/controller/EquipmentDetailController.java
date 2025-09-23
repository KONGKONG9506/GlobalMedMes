package com.globalmed.mes.mes_api.equipstatus.controller;

import com.globalmed.mes.mes_api.code.domain.ProcessEntity;
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
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/equip-detail")
@RequiredArgsConstructor
public class EquipmentDetailController {
    private final EquipmentRepo equipmentRepo;
    private final WorkOrderRepo workOrderRepo;
    private final ShiftAssignmentRepo shiftAssignRepo;
    private final EquipmentCertRepo equipCertRepo;
    private final ProcessCertRepo proCertRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeCertRepo employeeCertRepo;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @RequestParam String equipmentId, HttpServletRequest req) {
        try {
            EquipmentEntity equipment = equipmentRepo.findDetail(equipmentId);
            if (equipment == null) {
                // ID가 없는 경우, GlobalExceptionHandler 양식으로 반환
                return ResponseEntity.status(404).body(Map.of(
                        "code", "ID_NOT_FOUND",
                        "message", "해당 장비 ID를 찾을 수 없습니다",
                        "path", req.getRequestURI(),
                        "method", req.getMethod()
                ));
            }

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

            List<EquipmentCertEntity> equipCerts = equipCertRepo.findEquiprequier(equipmentId);
            List<CertEntity> requiredCerts = equipCerts.stream()
                    .map(EquipmentCertEntity::getCert) // CertEntity 꺼내기
                    .toList();
            List<EquipDetailDto.ECertDto> equipCertDtos = requiredCerts.stream()
                    .map(c -> new EquipDetailDto.ECertDto(
                            c.getCertCode(),
                            c.getCertName(),
                            c.getCertDescription()
                    ))
                    .toList();


            List<EquipDetailDto.EProcessDto> equipPro = List.of(
                    new EquipDetailDto.EProcessDto(
                            equipment.getProcess().getProcessId(),
                            equipment.getProcess().getProcessName(),
                            equipment.getProcess().getDescription()
                    )
            );

            List<ProcessCertEntity> proCert = proCertRepo.findProCert(equipment.getProcess().getProcessId());
            List<EmployeeCertEntity> employeeCerts = employeeCertRepo.findEmployeeCerts(shiftAssign.stream()
                    .map(ShiftAssignmentEntity::getWorkerId)
                    .toList());

            // 현재 작업자 자격증 ID 모음
            Set<Long> employeeCertIds = employeeCerts.stream()
                    .map(ec -> ec.getCert().getCertId())
                    .collect(Collectors.toSet());

            Map<String, Set<Long>> processRequiredCertIds = proCert.stream()
                    .collect(Collectors.groupingBy(
                            pc -> pc.getProcess().getProcessId(), // 공정 ID (String)
                            Collectors.mapping(pc -> pc.getCert().getCertId(), Collectors.toSet())
                    ));


            // 작업자가 수행 가능한 공정 필터링
            List<EquipDetailDto.EProcessDto> userEqPro = processRequiredCertIds.entrySet().stream()
                    .filter(entry -> employeeCertIds.containsAll(entry.getValue())) // 모든 요구 자격증 포함 여부
                    .map(entry -> {
                        // ProcessEntity 객체 가져오기 (proCert에서 첫번째 것 사용)
                        ProcessEntity process = proCert.stream()
                                .filter(pc -> pc.getProcess().getProcessId().equals(entry.getKey()))
                                .findFirst()
                                .get()
                                .getProcess();

                        return new EquipDetailDto.EProcessDto(
                                process.getProcessId(),
                                process.getProcessName(),
                                process.getDescription()
                        );
                    })
                    .toList();


            EquipDetailDto detaile = new EquipDetailDto(
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

            return ResponseEntity.ok(detaile);

        } catch (Exception e) {
            // 예외 발생 시 GlobalExceptionHandler 스타일로 반환
            return ResponseEntity.status(500).body(Map.of(
                    "code", "INTERNAL_ERROR",
                    "message", e.getMessage(),
                    "path", req.getRequestURI(),
                    "method", req.getMethod()
            ));
        }
    }
}
