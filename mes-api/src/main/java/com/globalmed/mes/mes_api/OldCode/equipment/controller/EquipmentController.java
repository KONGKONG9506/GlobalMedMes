package com.globalmed.mes.mes_api.OldCode.equipment.controller;

import com.globalmed.mes.mes_api.OldCode.equipment.dto.EquipmentResponseDto;
import com.globalmed.mes.mes_api.OldCode.equipment.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping("/{equipmentId}/status")
    public ResponseEntity<EquipmentResponseDto> changeStatus(
            @PathVariable("equipmentId") String equipmentId,
            @RequestParam(name = "statusCode") String statusCode, // 'RUN', 'IDLE', 'DOWN'
            Authentication authentication
    ) {
        String currentUserId = authentication.getName();
        EquipmentResponseDto dto = equipmentService.changeEquipmentStatus(equipmentId, statusCode, currentUserId);
        return ResponseEntity.ok(dto);
    }
}
