package com.globalmed.mes.mes_api.workorder.service;

import com.globalmed.mes.mes_api.commoncodegroup.commoncode.domain.CommonCodeEntity;
import com.globalmed.mes.mes_api.commoncodegroup.commoncode.repository.CommonCodeRepository;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderRequestDto;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private static final String WO_STATUS_GROUP = "WO_STATUS";
    private static final String STATUS_P = "P";

    private final WorkOrderRepository workOrderRepository;
    private final CommonCodeRepository commonCodeRepository;

    @Transactional
    public WorkOrderEntity createWorkOrder(WorkOrderRequestDto dto, String createdBy) {

        // 1) WO 번호 중복 체크
        if (workOrderRepository.existsByWorkOrderNumber(dto.getWorkOrderNumber())) {
            throw new IllegalArgumentException("이미 존재하는 작업지시 번호입니다: " + dto.getWorkOrderNumber());
        }

        // 2) 상태코드(P) 조회
        CommonCodeEntity statusP = commonCodeRepository
                .findByCodeGroup_GroupCodeAndCode(WO_STATUS_GROUP, STATUS_P)
                .orElseThrow(() -> new IllegalStateException("작업지시 상태코드(P)를 찾을 수 없습니다."));

        // 3) 엔티티 생성
        WorkOrderEntity wo = new WorkOrderEntity();
        wo.setWorkOrderId(UUID.randomUUID().toString());
        wo.setWorkOrderNumber(dto.getWorkOrderNumber());
        wo.setItemId(dto.getItemId());
        wo.setProcessId(dto.getProcessId());
        wo.setEquipmentId(dto.getEquipmentId());
        wo.setOrderQty(dto.getOrderQty());
        wo.setProducedQty(BigDecimal.ZERO);
        wo.setStatusCode(statusP);
        wo.setIsDeleted((byte)0);
        wo.setCreatedBy(createdBy);

        return workOrderRepository.save(wo);
    }
    @Transactional(readOnly = true)
    public WorkOrderEntity getWorkOrderById(String id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작업지시 ID입니다: " + id));
    }
}
