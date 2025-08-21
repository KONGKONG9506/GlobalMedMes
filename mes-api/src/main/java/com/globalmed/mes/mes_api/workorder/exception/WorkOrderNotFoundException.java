package com.globalmed.mes.mes_api.workorder.exception;

import com.globalmed.mes.mes_api.workorder.dto.WorkOrderMessageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class WorkOrderNotFoundException extends RuntimeException {
    public WorkOrderNotFoundException(String msg) {
        super(msg);
    }

    // 이미 존재하는 작업지시 번호
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WorkOrderMessageResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        WorkOrderMessageResponseDto response = new WorkOrderMessageResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 존재하지 않는 작업지시
    @ExceptionHandler(WorkOrderNotFoundException.class)
    public ResponseEntity<WorkOrderMessageResponseDto> handleWorkOrderNotFound(WorkOrderNotFoundException ex) {
        WorkOrderMessageResponseDto response = new WorkOrderMessageResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 그 외 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WorkOrderMessageResponseDto> handleException(Exception ex) {
        WorkOrderMessageResponseDto response = new WorkOrderMessageResponseDto("서버 오류: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}