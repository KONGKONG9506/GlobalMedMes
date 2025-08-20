package com.globalmed.mes.mes_api.workorder.controller;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderRequestDto;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderResponseDto;
import com.globalmed.mes.mes_api.workorder.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /**
     * 작업지시 생성
     */
    @PostMapping
    public ResponseEntity<WorkOrderEntity> createWorkOrder(
            @RequestBody WorkOrderRequestDto requestDto,
            @AuthenticationPrincipal String username) {

        String createdBy = (username != null) ? username : "system";
        WorkOrderEntity created = workOrderService.createWorkOrder(requestDto, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * (옵션) 작업지시 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponseDto> getWorkOrder(@PathVariable String id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id));
    }
}
