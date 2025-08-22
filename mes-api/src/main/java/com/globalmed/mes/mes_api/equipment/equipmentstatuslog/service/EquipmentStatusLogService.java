package com.globalmed.mes.mes_api.equipment.equipmentstatuslog.service;

import com.globalmed.mes.mes_api.equipment.equipmentstatuslog.domain.EquipmentStatusLogEntity;
import com.globalmed.mes.mes_api.equipment.equipmentstatuslog.dto.EquipmentStatusLogResponse;
import com.globalmed.mes.mes_api.equipment.equipmentstatuslog.repository.EquipmentStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentStatusLogService {

    private final EquipmentStatusLogRepository logRepository;

    public List<EquipmentStatusLogResponse> getLogsByEquipmentId(String equipmentId) {
        List<EquipmentStatusLogEntity> logs = logRepository.findByEquipment_EquipmentIdOrderByStartTimeDesc(equipmentId);

        return logs.stream()
                .map(log -> EquipmentStatusLogResponse.builder()
                        .logId(log.getLogId())
                        .equipmentId(log.getEquipment().getEquipmentId())
                        .equipmentName(log.getEquipment().getEquipmentName())
                        .statusCode(log.getStatusCode().getCode())
                        .statusName(log.getStatusCode().getName())
                        .reasonCode(log.getReasonCode() != null ? log.getReasonCode().getCode() : null)
                        .startTime(log.getStartTime())
                        .endTime(log.getEndTime())
                        .createdBy(log.getCreatedBy())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
