package com.globalmed.mes.mes_api.workorder.service;

import com.globalmed.mes.mes_api.commoncodegroup.commoncode.domain.CommonCodeEntity;
import com.globalmed.mes.mes_api.commoncodegroup.commoncode.repository.CommonCodeRepository;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderListResponseDto;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderRequestDto;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderResponseDto;
import com.globalmed.mes.mes_api.workorder.exception.WorkOrderNotFoundException;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private static final String WO_STATUS_GROUP = "WO_STATUS";
    private static final String STATUS_P = "P";

    private final WorkOrderRepository workOrderRepository;
    private final CommonCodeRepository commonCodeRepository;

    @Transactional(readOnly = true)
    public WorkOrderResponseDto getWorkOrderByNumber(String workOrderNumber) {
        WorkOrderEntity entity = workOrderRepository.findByWorkOrderNumber(workOrderNumber)
                .orElseThrow(() -> new WorkOrderNotFoundException("해당 작업지시가 존재하지 않습니다: " + workOrderNumber));

        return WorkOrderResponseDto.fromEntity(entity);
    }
    @Transactional(readOnly = true)
    public WorkOrderResponseDto getWorkOrderById(String workOrderId) {
        WorkOrderEntity entity = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFoundException("해당 작업지시가 존재하지 않습니다: " + workOrderId));

        return WorkOrderResponseDto.fromEntity(entity);
    }

    @Transactional
    public WorkOrderResponseDto updateWorkOrderStatus(String workOrderId, String toStatus, String updatedBy) {
        WorkOrderEntity wo = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFoundException("해당 작업지시가 존재하지 않습니다: " + workOrderId));

        // 먼저 입력값이 유효한 상태코드인지 확인
        CommonCodeEntity newStatus = commonCodeRepository.findByCodeGroup_GroupCodeAndCode(WO_STATUS_GROUP, toStatus)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상태코드: " + toStatus));

        String currentStatus = wo.getStatusCode().getCode(); // 현재 상태 P, R, C

        // 현재 상태와 동일하면
        if (currentStatus.equals(toStatus)) {
            throw new IllegalArgumentException("현재 상태와 동일합니다: " + currentStatus);
        }

        // 허용되지 않은 상태 전환 체크
        if (!isValidTransition(currentStatus, toStatus)) {
            throw new IllegalArgumentException(
                    "상태 전환 불가: 현재 상태=" + currentStatus + ", 요청 상태=" + toStatus
            );
        }

        wo.setStatusCode(newStatus);
        wo.setModifiedBy(updatedBy);

        WorkOrderEntity saved = workOrderRepository.save(wo);
        return WorkOrderResponseDto.fromEntity(saved);
    }

    // 상태 전환 유효성 체크
    private boolean isValidTransition(String currentStatus, String toStatus) {
        return (currentStatus.equals("P") && (toStatus.equals("R") || toStatus.equals("C"))) ||
                (currentStatus.equals("R") && toStatus.equals("C"));
    }


    @Transactional(readOnly = true)
    public List<WorkOrderListResponseDto> getAllWorkOrders() {
        return workOrderRepository.findAll().stream()
                .map(wo -> new WorkOrderListResponseDto(
//      지금 리스트에 전부다 보내는 중 일부만 보낼거면 DTO와 이부분에서 보내지 않을 항목 삭제
                        wo.getWorkOrderId(),
                        wo.getWorkOrderNumber(),
                        wo.getStatusCode().getCode(),
                        wo.getStatusCode().getName(),
                        wo.getItemId(),
                        wo.getEquipmentId(),
                        wo.getProcessId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkOrderEntity createWorkOrderEntity(WorkOrderRequestDto dto, String createdBy) {
        if (workOrderRepository.existsByWorkOrderNumber(dto.getWorkOrderNumber())) {
            throw new IllegalArgumentException("이미 존재하는 작업지시 번호입니다: " + dto.getWorkOrderNumber());
        }

        CommonCodeEntity statusP = commonCodeRepository
                .findByCodeGroup_GroupCodeAndCode(WO_STATUS_GROUP, STATUS_P)
                .orElseThrow(() -> new IllegalStateException("작업지시 상태코드(P)를 찾을 수 없습니다."));

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
}
