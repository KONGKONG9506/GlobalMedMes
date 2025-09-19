//package com.globalmed.mes.mes_api.equipstatus.controller;
//
//import com.globalmed.mes.mes_api.code.repository.ProcessRepo;
//import com.globalmed.mes.mes_api.common.PageResponse;
//import com.globalmed.mes.mes_api.employee.cert.repository.CertRepo;
//import com.globalmed.mes.mes_api.employee.cert.repository.EquipmentCertRepo;
//import com.globalmed.mes.mes_api.employee.cert.repository.ProcessCertRepo;
//import com.globalmed.mes.mes_api.employee.repository.EmployeeCertRepo;
//import com.globalmed.mes.mes_api.employee.shift.repository.ShiftAssignmentRepo;
//import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
//import com.globalmed.mes.mes_api.equipstatus.dto.EquipDetailDto;
//import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
//import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
//import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/equip")
//@RequiredArgsConstructor
//public class EquipmentDetailController {
//    private final EquipmentRepo equipmentRepo;
//    private final WorkOrderRepo workOrderRepo;
//    private final ShiftAssignmentRepo shiftAssignRepo;
//    private final CertRepo certRepo;
//    private final EquipmentCertRepo equipCertRepo;
//    private final ProcessRepo processRepo;
//    private final ProcessCertRepo proCertRepo;
//    private final EmployeeCertRepo employeeCertRepo;
//
//    @GetMapping
//    public ResponseEntity<PageResponse<EquipDetailDto>> list(
//            @RequestParam String equipmentId) {
//        EquipmentEntity equipment = equipmentRepo.findDetail(equipmentId);
//        Optional<WorkOrderEntity> lastWorkOredr = workOrderRepo.findFirstByEquipment_EquipmentIdOrderByCreatedAtDesc(equipmentId);
//        shiftAssign = shiftAssignRepo.findEquipShiftNow(equipmentId);
//        equipCert = equipCertRepo.findEquiprequier(equipmentId);
//        employeeCert = employeeCertRepo.findEmployeeCert();
//
//        detaile = new EquipDetailDto(
//                equipmentId,
//                equipment.getEquipmentName(),
//                equipment.getWorkcenter(),
//                equipment.getStatusCode(),
//                equipment.getCreatedAt(),
//                workOrederName,
//                itemName,
//                produceQty,
//                orderQty,
//                workers,
//                shiftName,
//                shiftStartTs,
//                shiftEndTs,
//                requiredCerts,
//                equipProcess,
//                userEProcess
//        )
//
//
//        return null;
//    }
//}
