// com.globalmed.mes.mes_api.integration.erp.ErpApiClient.java (MES 백엔드)

package com.globalmed.mes.mes_api.integration.erp;

import com.globalmed.mes.mes_api.integration.erp.dto.WorkOrderStatusFeedbackDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ErpApiClient {

    private final WebClient webClient;
    private static final String ERP_FEEDBACK_URI = "/api/mes/feedback/status";

    // ERP 상태 업데이트를 위한 DTO (Plan ID, 신규 상태, 수정자)
    public record ErpStatusUpdateDto(String newStatus, String modifier) {}

    public ErpApiClient(@Value("${erp.api.base-url}") String erpApiBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(erpApiBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * ERP에 생산 계획 상태 업데이트를 요청합니다.
     * ERP API: PUT /api/erp/production-plans/{planId}/status
     */
    public Mono<String> updateErpPlanStatus(String planId, String newStatus, String modifier) {

        ErpStatusUpdateDto updateDto = new ErpStatusUpdateDto(newStatus, modifier);

        return webClient.put()
                .uri("/api/erp/production-plans/{planId}/status", planId)
                .body(BodyInserters.fromValue(updateDto))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("ERP 상태 업데이트 실패: " + errorBody))))
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("ERP 상태 업데이트 실패 (Plan ID: " + planId + "): " + e.getMessage());
                    return Mono.error(e);
                });
    }

    /**
     * Work Order 상태 변경 정보를 ERP로 전송하여 Plan 상태를 업데이트합니다.
     */
    public Mono<String> sendStatusUpdate(WorkOrderStatusFeedbackDto feedbackDto) {
        return webClient.post()
                .uri(ERP_FEEDBACK_URI)
                .body(BodyInserters.fromValue(feedbackDto))
                .retrieve()
                // 🚨 에러 처리 로직 추가 필요 (ERP 통신 실패 시)
                .bodyToMono(String.class);
    }
}