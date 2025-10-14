package com.globalmed.mes.mes_api.workorder.service;

import com.globalmed.mes.mes_api.integration.erp.ErpApiClient;
import com.globalmed.mes.mes_api.integration.erp.dto.WorkOrderStatusFeedbackDto;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ToErpStatusService {

    private final ErpApiClient erpApiClient;

    // ERP 상태 코드 상수 정의
    private static final String ERP_PENDING = "PENDING";
    private static final String ERP_IN_PRODUCTION = "IN_PRODUCTION";
    private static final String ERP_COMPLETED = "COMPLETED";


    /**
     * Work Order 엔티티를 기반으로 피드백 DTO를 생성하고 ERP로 전송합니다.
     * @param wo 업데이트된 Work Order 엔티티
     */
    public void pushStatusToErp(WorkOrderEntity wo) {

        // 1. MES Work Order 상태를 ERP Plan 상태로 변환
        String mesWoStatusCode = wo.getStatusCode().getCode(); // CodeEntity에서 실제 코드 값(P, R, C)을 가져온다고 가정
        String erpPlanStatus = mapMesStatusToErpPlanStatus(mesWoStatusCode);

        // 2. 피드백 DTO 생성
        WorkOrderStatusFeedbackDto feedbackDto = new WorkOrderStatusFeedbackDto(
                wo.getPlanId(),
                wo.getWorkOrderId(),
                wo.getWorkOrderNumber(),
                erpPlanStatus,             // 🚨 ERP가 업데이트할 상태 (IN_PRODUCTION, COMPLETED)
                mesWoStatusCode,           // MES의 Work Order 상태 (P, R, C)
                wo.getProducedQty(),
                OffsetDateTime.now()
        );

        // 3. ERP API 호출
        try {
            // WebClient의 Mono<String>을 블로킹하여 동기적으로 호출
            String erpResponse = erpApiClient.sendStatusUpdate(feedbackDto).block();
            System.out.println("ERP 상태 피드백 성공: " + erpResponse);

        } catch (Exception e) {
            // ERP 통신 실패 시 처리
            System.err.println("경고: ERP로의 상태 피드백 실패 (Work Order: " + wo.getWorkOrderId() + "): " + e.getMessage());
        }
    }

    /**
     * MES Work Order 상태 코드를 ERP Production Plan 상태 코드로 변환하는 맵핑 로직
     */
    private String mapMesStatusToErpPlanStatus(String mesStatusCode) {
        // MES는 P(Planned), R(Released/Running), C(Completed)를 사용한다고 가정합니다.

        return switch (mesStatusCode) {
            case "R" -> ERP_IN_PRODUCTION; // R (Released) -> IN_PRODUCTION (작업 시작)
            case "C" -> ERP_COMPLETED;     // C (Completed) -> COMPLETED (작업 완료)
            default -> ERP_PENDING;        // 정의되지 않은 상태는 PENDING으로 처리
        };
    }
}