package com.globalmed.mes.mes_api.process.productionperformance.controller;

import com.globalmed.mes.mes_api.process.productionperformance.dto.ProductionPerformanceRequestDto;
import com.globalmed.mes.mes_api.process.productionperformance.dto.ProductionPerformanceResponseDto;
import com.globalmed.mes.mes_api.process.productionperformance.service.ProductionPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class ProductionPerformanceController {

    private final ProductionPerformanceService performanceService;

    @PostMapping
    public ResponseEntity<ProductionPerformanceResponseDto> createPerformance(
            @RequestBody ProductionPerformanceRequestDto dto,
            Authentication authentication
    ) {
        String userId = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "system";
        ProductionPerformanceResponseDto response = performanceService.createPerformance(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단일 퍼포먼스 조회
    @GetMapping("/{performanceId}")
    public ResponseEntity<?> getPerformanceById(@PathVariable("performanceId") Long performanceId) {
        try {
            ProductionPerformanceResponseDto dto = performanceService.getPerformanceById(performanceId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 퍼포먼스를 찾을 수 없습니다: " + ex.getMessage());
        }
    }

    // 워크오더 기준 퍼포먼스 리스트 조회
    @GetMapping("/by-workorder/{workOrderId}")
    public ResponseEntity<?> getPerformancesByWorkOrder(@PathVariable("workOrderId") String workOrderId) {
        try {
            var list = performanceService.getPerformancesByWorkOrder(workOrderId);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 워크오더의 퍼포먼스를 찾을 수 없습니다: " + ex.getMessage());
        }
    }
}
