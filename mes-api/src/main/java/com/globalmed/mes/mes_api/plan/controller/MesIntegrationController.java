package com.globalmed.mes.mes_api.plan.controller;

import com.globalmed.mes.mes_api.integration.erp.dto.ProductionPlanDto;
import com.globalmed.mes.mes_api.plan.service.MesPlanIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mes/production-plans") // 🚨 ERP MesApiClient가 호출하는 경로
@RequiredArgsConstructor
public class MesIntegrationController {

    private final MesPlanIntegrationService integrationService;

    /**
     * ERP로부터 생산 계획을 수신하여 MES DB에 저장하고 워크 오더를 생성합니다.
     * @param planDto ERP 생산 계획 데이터
     * @return 성공 메시지
     */
    @PostMapping
    public ResponseEntity<String> syncProductionPlan(@RequestBody ProductionPlanDto planDto) {

        // 🚨 권한 검증: 이 컨트롤러가 401을 반환하는 보안 필터를 통과해야 합니다.
        // (현재 문제의 원인이므로, 이 코드는 보안 통과 후 실행된다고 가정합니다.)

        integrationService.processIncomingPlan(planDto);

        // ERP의 WebClient가 Mono<String>을 기대하므로, 간단한 문자열 응답을 반환
        return ResponseEntity.ok("Production Plan synchronized successfully.");
    }
}