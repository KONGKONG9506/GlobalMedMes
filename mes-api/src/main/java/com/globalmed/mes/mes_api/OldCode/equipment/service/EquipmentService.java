package com.globalmed.mes.mes_api.OldCode.equipment.service;

import com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.domain.CommonCodeEntity;
import com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.repository.CommonCodeRepository;
import com.globalmed.mes.mes_api.OldCode.equipment.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.OldCode.equipment.dto.EquipmentResponseDto;
import com.globalmed.mes.mes_api.OldCode.equipment.equipmentstatuslog.domain.EquipmentStatusLogEntity;
import com.globalmed.mes.mes_api.OldCode.equipment.equipmentstatuslog.repository.EquipmentStatusLogRepository;
import com.globalmed.mes.mes_api.OldCode.equipment.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentStatusLogRepository logRepository;
    private final CommonCodeRepository codeRepository;

    @Transactional
    public EquipmentResponseDto changeEquipmentStatus(
            String equipmentId,
            String newStatusCode,
            String currentUserId
    ) {
        EquipmentEntity equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("설비를 찾을 수 없습니다: " + equipmentId));

        CommonCodeEntity statusCode = codeRepository
                .findByCodeGroup_GroupCodeAndCode("EQP_STATUS", newStatusCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상태 코드: " + newStatusCode));

        // 상태 변경
        equipment.setStatusCode(statusCode);
        equipment.setModifiedBy(currentUserId);
        equipmentRepository.save(equipment);

        // 로그 생성
        EquipmentStatusLogEntity log = new EquipmentStatusLogEntity();
        log.setEquipment(equipment);
        log.setStatusCode(statusCode);
        log.setStartTime(LocalDateTime.now());
        log.setCreatedBy(currentUserId);
        logRepository.save(log);

        // DTO 변환
        EquipmentResponseDto dto = new EquipmentResponseDto();
        dto.setEquipmentId(equipment.getEquipmentId());
        dto.setEquipmentName(equipment.getEquipmentName());
        dto.setStatusCode(statusCode.getCode());
        dto.setStatusName(statusCode.getName());

        return dto;
    }}
