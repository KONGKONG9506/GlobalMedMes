package com.globalmed.mes.mes_api.plan.controller;

import com.globalmed.mes.mes_api.plan.dto.ErpPlanDetailResponseDto;
import com.globalmed.mes.mes_api.plan.dto.ErpPlanListDto;
import com.globalmed.mes.mes_api.plan.service.ErpPlanQueryService; // 새 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ERP에서 전송받은 생산 계획 (Production Plan)을 조회하는 Controller
 */
@RestController
@RequestMapping("/api/mes/production-plans") // 🚨 ERP 연동 전용 경로
@RequiredArgsConstructor
public class ErpPlanController {

    private final ErpPlanQueryService erpPlanQueryService;

    /**
     * ERP에서 수신된 Production Plan 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ErpPlanListDto>> getPlanList() {
        // TODO: 실제로는 페이징 및 검색 조건 추가 필요
        List<ErpPlanListDto> plans = erpPlanQueryService.findAllPlans();
        return ResponseEntity.ok(plans);
    }

    /**
     * 특정 Production Plan의 상세 정보와 연결된 Work Order 목록 조회
     */
    @GetMapping("/{planId}")
    public ResponseEntity<ErpPlanDetailResponseDto> getPlanDetail(@PathVariable String planId) {
        ErpPlanDetailResponseDto detail = erpPlanQueryService.findPlanDetail(planId);
        return ResponseEntity.ok(detail);
    }
}