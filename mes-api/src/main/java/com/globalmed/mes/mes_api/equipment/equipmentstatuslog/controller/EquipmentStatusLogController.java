package com.globalmed.mes.mes_api.equipment.equipmentstatuslog.controller;

import com.globalmed.mes.mes_api.equipment.equipmentstatuslog.dto.EquipmentStatusLogResponse;
import com.globalmed.mes.mes_api.equipment.equipmentstatuslog.service.EquipmentStatusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentStatusLogController {

    private final EquipmentStatusLogService logService;

    @GetMapping("/{equipmentId}/logs")
    public ResponseEntity<List<EquipmentStatusLogResponse>> getLogs(
            @PathVariable String equipmentId
    ) {
        List<EquipmentStatusLogResponse> logs = logService.getLogsByEquipmentId(equipmentId);
        return ResponseEntity.ok(logs);
    }
}
