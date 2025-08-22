package com.globalmed.mes.mes_api.workorder.controller;

import com.globalmed.mes.mes_api.workorder.dto.*;
import com.globalmed.mes.mes_api.workorder.exception.WorkOrderNotFoundException;
import com.globalmed.mes.mes_api.workorder.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /**
     * 작업지시 생성
     */
    @PostMapping
    public ResponseEntity<WorkOrderMessageResponseDto> createWorkOrder(
            @RequestBody WorkOrderRequestDto requestDto,
            Authentication authentication) {

        String createdBy = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "system";

        try {
            workOrderService.createWorkOrderEntity(requestDto, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new WorkOrderMessageResponseDto("작업지시가 성공적으로 작성되었습니다."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WorkOrderMessageResponseDto(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WorkOrderMessageResponseDto("예외 서버 오류: " + ex.getMessage()));
        }
    }


    /**
     * (옵션) 작업지시 단건 조회
     */
    @GetMapping("/by-number/{workOrderNumber}")
    public ResponseEntity<?> getWorkOrderByNumber(@PathVariable("workOrderNumber") String workOrderNumber) {
        try {
            WorkOrderResponseDto dto = workOrderService.getWorkOrderByNumber(workOrderNumber);
            return ResponseEntity.ok(dto);
        } catch (WorkOrderNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WorkOrderMessageResponseDto(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WorkOrderMessageResponseDto("예외 서버 오류: " + ex.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<List<WorkOrderListResponseDto>> getAllWorkOrders() {
        List<WorkOrderListResponseDto> list = workOrderService.getAllWorkOrders();
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{workOrderId}")
    public ResponseEntity<?> getWorkOrderById(@PathVariable("workOrderId") String workOrderId) {
        try {
            WorkOrderResponseDto dto = workOrderService.getWorkOrderById(workOrderId);
            return ResponseEntity.ok(dto);
        } catch (WorkOrderNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WorkOrderMessageResponseDto(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WorkOrderMessageResponseDto("예외 서버 오류: " + ex.getMessage()));
        }
    }

    @PutMapping("/{workOrderId}/status")
    public ResponseEntity<WorkOrderMessageResponseDto> updateWorkOrderStatus(
            @PathVariable("workOrderId") String workOrderId,  // 변수 이름 명시
            @RequestBody WorkOrderStatusUpdateRequestDto request,
            Authentication authentication) {

        String updatedBy = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "system";

        try {
            workOrderService.updateWorkOrderStatus(workOrderId, request.getToStatus(), updatedBy);
            return ResponseEntity.ok(new WorkOrderMessageResponseDto("작업지시 상태가 '" + request.getToStatus() + "'로 변경되었습니다."));
        } catch (WorkOrderNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new WorkOrderMessageResponseDto(ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new WorkOrderMessageResponseDto(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new WorkOrderMessageResponseDto("서버 오류: " + ex.getMessage()));
        }
    }
}
