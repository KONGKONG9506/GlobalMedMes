// com.globalmed.mes.mes_api.workorder.service.WorkOrderService.java
package com.globalmed.mes.mes_api.workorder.service;


import com.globalmed.mes.mes_api.code.CodeEntity;
import com.globalmed.mes.mes_api.code.CodeRepo;
import com.globalmed.mes.mes_api.process.repository.ProcessRepo;
import com.globalmed.mes.mes_api.item.ItemRepo;
import com.globalmed.mes.mes_api.employee.cert.service.ProcessCertCheckService;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import com.globalmed.mes.mes_api.production.service.ProductionLogService;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderDetailDto;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderListDto;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderService {
    private final WorkOrderRepo woRepo;
    private final CodeRepo codeRepo;
    private final ItemRepo itemRepo;
    private final ProcessRepo processRepo;
    private final EquipmentRepo equipmentRepo;
    private final ProductionLogService productionLogService;
    private final ProcessCertCheckService processCertCheckService;
    private final ToErpStatusService toErpStatusService;

    @Transactional
    public WorkOrderEntity create(String planId, String workOrderNumber, String itemId, String processId,
                                  String equipmentId, BigDecimal orderQty, String createdByOpt) {

        woRepo.findByWorkOrderNumber(workOrderNumber).ifPresent(x -> {
            throw new IllegalStateException("DUPLICATE_KEY");
        });

        // 1. 각 ID로 관련 엔티티 객체 조회
        var item = itemRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("ITEM_NOT_FOUND"));
        var process = processRepo.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("PROCESS_NOT_FOUND"));
        var equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("EQUIPMENT_NOT_FOUND"));

        // 상태코드 P, use_yn='Y'
        var status = codeRepo.findByGroupCodeAndCodeAndUseYn("WO_STATUS", "P", 'Y')
                .orElseThrow(() -> new IllegalStateException("WO_STATUS_P_NOT_FOUND"));

        var wo = new WorkOrderEntity();
        wo.setWorkOrderId(UUID.randomUUID().toString());
        wo.setWorkOrderNumber(workOrderNumber);

        // planId가 null 또는 비어 있지 않을 때만 설정
        if (planId != null && !planId.isBlank()) {
            wo.setPlanId(planId);
        }

        wo.setItemId(item);
        wo.setProcessId(process);
        wo.setEquipmentId(equipment);
        wo.setOrderQty(orderQty);
        wo.setProducedQty(BigDecimal.ZERO);
        wo.setStatusCode(status);        // ← status_code_id 매핑 완료
        if (createdByOpt != null && !createdByOpt.isBlank()) {
            wo.setCreatedBy(createdByOpt); // 값 있으면 사용, 없으면 @PrePersist에서 자동 세팅
        }

        return woRepo.save(wo);
    }

    @Transactional
    public WorkOrderEntity transition(String workOrderId, String toStatus, OffsetDateTime now) {
        WorkOrderEntity wo = woRepo.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        String cur = wo.getStatusCode().getCode();         // 현재 P/R/C
        String to  = toStatus != null ? toStatus.trim() : "";
        if(now == null) now = OffsetDateTime.now();

        // 허용 전이만 통과
        boolean allowed = (cur.equals("P") && to.equals("R"))
                || (cur.equals("R") && to.equals("C"));
        if (!allowed) {
            throw new IllegalStateException("WO_STATUS_INVALID");
        }
        if(now == null) now = OffsetDateTime.now();
        if(cur.equals("P")&&to.equals("R")){
        }

//            공정 자격 체크는 일단 제외
//             processCertCheckService.check(wo.getEquipmentId().getEquipmentId(),wo.getProcessId().getProcessId(), now);

        // 상태 코드(P/R/C) 조회(use_yn='Y'), group_code는 네 DB 기준으로(소문자/대문자)
        CodeEntity next = codeRepo.findByGroupCodeAndCodeAndUseYn("wo_status", to, 'Y')
                .orElseThrow(() -> new IllegalStateException("WO_STATUS_"+to+"_NOT_FOUND"));
        wo.setStatusCode(next);           // status_code_id 매핑

        // ✅ 상태 전이에 따른 로그 기록
        if (cur.equals("P") && to.equals("R")) {
            // Released → START: startTs 설정
            wo.setStartTs(now.toLocalDateTime());
            // START 로그 기록
            productionLogService.logStart(
                    wo.getWorkOrderId(),
                    wo.getEquipmentId().getEquipmentId(),
                    wo.getProcessId().getProcessId()
            );
        } else if (cur.equals("R") && to.equals("C")) {
            // Completed → END: endTs 설정
            wo.setEndTs(now.toLocalDateTime());
            // END 로그 기록
            productionLogService.logEnd(
                    wo.getWorkOrderId(),
                    wo.getEquipmentId().getEquipmentId(),
                    wo.getProcessId().getProcessId()
            );
        }

        // 3. 🚨 ERP 통보 (PlanId가 있을 경우에만 실행)
        if (wo.getPlanId() != null && !wo.getPlanId().isBlank()) {
            // ToErpStatusService는 Work Order 엔티티를 받아 ERP 상태로 변환 및 PUSH 처리를 수행합니다.
            toErpStatusService.pushStatusToErp(wo);
        }

        return wo;
    }
    // @Transactional로 플러시
    @Transactional
    public WorkOrderDetailDto findById(String workOrderId) {
        WorkOrderEntity wo = woRepo.findByIdWithDetails(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));
        return WorkOrderDetailDto.fromEntity(wo);
    }

    @Transactional
    public List<WorkOrderListDto> findAllWithDetails(Specification<WorkOrderEntity> spec) {
        List<WorkOrderEntity> filtered = woRepo.findAll(spec);

        List<WorkOrderEntity> workOrders = woRepo.findAllwithDetails();

        // 엔티티 리스트를 DTO 리스트로 변환
        return workOrders.stream()
                .map(WorkOrderListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * PlanId 없이 Work Order를 수동으로 생성할 때 사용되는 오버로딩 메소드
     */
    @Transactional
    public WorkOrderEntity create(String workOrderNumber, String itemId, String processId,
                                  String equipmentId, BigDecimal orderQty, String createdByOpt) {
        // planId에 null을 전달하여 기존 메소드를 호출
        return create(null, workOrderNumber, itemId, processId, equipmentId, orderQty, createdByOpt);
    }
}