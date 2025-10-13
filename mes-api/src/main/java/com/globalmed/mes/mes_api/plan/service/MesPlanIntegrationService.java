package com.globalmed.mes.mes_api.plan.service;

import com.globalmed.mes.mes_api.code.CodeRepo;
import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.item.ItemEntity;
import com.globalmed.mes.mes_api.item.ItemRepo;
import com.globalmed.mes.mes_api.process.repository.ProcessRepo;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import com.globalmed.mes.mes_api.plan.domain.TbProductionPlan; // MES의 Plan Entity
import com.globalmed.mes.mes_api.process.domain.ProcessEntity;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;      // MES의 WorkOrder Entity
import com.globalmed.mes.mes_api.integration.erp.dto.ProductionPlanDto;
import com.globalmed.mes.mes_api.plan.repository.ProductionPlanRepository;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MesPlanIntegrationService {

    private final ProductionPlanRepository planRepository;
    private final WorkOrderRepo workOrderRepository;
    private final CodeRepo codeRepo;
    private final ItemRepo itemRepo;
    private final ProcessRepo processRepo;
    private final EquipmentRepo equipmentRepo;

    // 필요한 경우 Item/Product 정보 조회를 위한 ItemService 등 추가 가능

    // MES 상태 코드 상수
    private static final String PLANNED = "P";
    private static final String RELEASED = "R";
    private static final String ITEM_DEFAULT_LINE = "P-100"; // 기본 생산 라인 ID 가정
    private static final String ITEM_DEFAULT_EQUIPMENT = "BLS-001"; // 기본 생산 라인 ID 가정

    /**
     * ERP에서 수신된 생산 계획 데이터를 MES DB에 반영하고 워크 오더를 생성합니다.
     * @param planDto ERP 생산 계획 DTO
     */
    @Transactional
    public void processIncomingPlan(ProductionPlanDto planDto) {

        // 1. 기존 계획 확인 (Plan ID는 ERP와 MES가 공유하는 UUID)
        TbProductionPlan mesPlan = planRepository.findById(planDto.getPlanId())
                .orElseGet(TbProductionPlan::new); // 없으면 새로 생성

        // 2. MES Production Plan Entity에 매핑 및 저장/업데이트
        // 새 계획이거나 업데이트 요청일 수 있음

        boolean isNew = mesPlan.getPlanId() == null;

        // 필드 매핑 (MES의 필드명에 맞게 변환)
        mesPlan.setPlanId(planDto.getPlanId());         // ERP UUID 사용
        mesPlan.setPlanNumber(planDto.getPlanCode());   // ERP planCode -> MES plan_number
        mesPlan.setItemId(planDto.getProductId());      // ERP productId -> MES item_id
        mesPlan.setTargetQty(planDto.getQty());         // ERP qty -> MES target_qty
        mesPlan.setStartDate(planDto.getStartDate());
        mesPlan.setEndDate(planDto.getEndDate());
        mesPlan.setStatus(PLANNED); // MES에서는 수신되면 PLANNED(P) 상태로 설정

        // 생성자 정보 기록
        if (isNew) {
            mesPlan.setCreatedBy(planDto.getCreatedBy());
        }
        mesPlan.setModifiedBy(planDto.getCreatedBy());

        planRepository.save(mesPlan);

        // 3. 워크 오더(Work Order) 자동 생성
        // MES는 계획 수립 시 즉시 워크 오더를 생성한다고 가정 (MES 비즈니스 로직)
        if (isNew) {
            createInitialWorkOrder(mesPlan);
        }
    }

    /**
     * MES 계획 수립에 따른 초기 워크 오더 생성 로직
     */
    private void createInitialWorkOrder(TbProductionPlan mesPlan) {
        WorkOrderEntity wo = new WorkOrderEntity();
        ItemEntity itemEntity = itemRepo.findById(mesPlan.getItemId())
                .orElseThrow(() -> new IllegalStateException("MES Item을 찾을 수 없습니다: " + mesPlan.getItemId()));
        var status = codeRepo.findByGroupCodeAndCodeAndUseYn("WO_STATUS", "P", 'Y')
                .orElseThrow(() -> new IllegalStateException("WO_STATUS_P_NOT_FOUND"));
        ProcessEntity processEntity = processRepo.findById(ITEM_DEFAULT_LINE)
                .orElseThrow(() -> new IllegalStateException("MES Process(공정/라인)을 찾을 수 없습니다: " + ITEM_DEFAULT_LINE));
        EquipmentEntity equipmentEntity = equipmentRepo.findById(ITEM_DEFAULT_EQUIPMENT)
                .orElseThrow(() -> new IllegalStateException("MES Equipment을 찾을 수 없습니다: " + ITEM_DEFAULT_EQUIPMENT));


        // Work Order 필드 설정 (단순 예시)
        wo.setWorkOrderId(UUID.randomUUID().toString());
        wo.setPlanId(mesPlan.getPlanId());
        wo.setWorkOrderNumber(mesPlan.getPlanNumber() + "-WO-001");
        wo.setItemId(itemEntity);
        wo.setProcessId(processEntity); // 아이템별 기본 라인 조회 로직 필요
        wo.setEquipmentId(equipmentEntity);
        wo.setOrderQty(mesPlan.getTargetQty());
        wo.setStartTs(LocalDateTime.now());
        LocalDateTime endDateTime = mesPlan.getEndDate().atTime(LocalTime.MAX);
        wo.setEndTs(endDateTime);
        wo.setStatusCode(status); // 워크 오더는 생성과 동시에 RELEASED(R) 상태로 설정
        wo.setCreatedBy(mesPlan.getCreatedBy());

        workOrderRepository.save(wo);
    }
}