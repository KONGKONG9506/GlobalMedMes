package com.globalmed.mes.mes_api.equipment.controller;

import com.globalmed.mes.mes_api.equipment.equipmentstatus.dto.EquipmentStatusRequestDto;
import com.globalmed.mes.mes_api.equipment.equipmentstatus.service.EquipmentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/equip-status")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentStatusService equipmentStatusService;

    @PostMapping
    public ResponseEntity<String> createEquipmentStatus(
            @RequestBody EquipmentStatusRequestDto requestDto,
            Authentication authentication) {

        // 로그인 사용자명 or system
        String createdBy = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "system";

        try {
            equipmentStatusService.recordStatus(requestDto, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("설비 상태가 기록되었습니다: " + requestDto.getStatusCode());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 요청: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + ex.getMessage());
        }
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<?> getEquipmentStatus(@PathVariable("equipmentId") String equipmentId) {
        try {
            var logs = equipmentStatusService.getStatusLogsByEquipment(equipmentId);
            return ResponseEntity.ok(logs);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 요청: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + ex.getMessage());
        }
    }
}
