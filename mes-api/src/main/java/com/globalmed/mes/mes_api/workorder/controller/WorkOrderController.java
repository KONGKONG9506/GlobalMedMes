package com.globalmed.mes.mes_api.workorder.controller;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.dto.WorkOrderRequestDto;
import com.globalmed.mes.mes_api.workorder.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WorkOrderEntity> createWorkOrder(@RequestBody WorkOrderRequestDto requestDto) {
        String createdBy = "system"; // 추후 Authentication에서 가져오면 됨
        WorkOrderEntity created = workOrderService.createWorkOrder(requestDto, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * (옵션) 작업지시 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderEntity> getWorkOrder(@PathVariable String id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id));
    }
}
