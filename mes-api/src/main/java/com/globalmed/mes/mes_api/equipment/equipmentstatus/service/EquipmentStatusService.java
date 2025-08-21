package com.globalmed.mes.mes_api.equipment.equipmentstatus.service;

import com.globalmed.mes.mes_api.commoncodegroup.commoncode.domain.CommonCodeEntity;
import com.globalmed.mes.mes_api.commoncodegroup.commoncode.repository.CommonCodeRepository;
import com.globalmed.mes.mes_api.equipment.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipment.equipmentstatus.domain.EquipmentStatusLogEntity;
import com.globalmed.mes.mes_api.equipment.equipmentstatus.dto.EquipmentStatusLogDto;
import com.globalmed.mes.mes_api.equipment.equipmentstatus.dto.EquipmentStatusRequestDto;
import com.globalmed.mes.mes_api.equipment.repository.EquipmentRepository;
import com.globalmed.mes.mes_api.equipment.equipmentstatus.repository.EquipmentStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentStatusService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentStatusLogRepository statusLogRepository;
    private final CommonCodeRepository commonCodeRepository;

    @Transactional
    public void recordStatus(EquipmentStatusRequestDto dto, String createdBy) {
        EquipmentEntity equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설비 ID: " + dto.getEquipmentId()));

        CommonCodeEntity statusCode = commonCodeRepository
                .findByCodeGroup_GroupCodeAndCode("EQP_STATUS", dto.getStatusCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상태 코드: " + dto.getStatusCode()));

        EquipmentStatusLogEntity log = new EquipmentStatusLogEntity();
        log.setEquipment(equipment);
        log.setStatusCode(statusCode);
        log.setStartTime(LocalDateTime.now(ZoneOffset.UTC)); // UTC 시간
        log.setCreatedBy(createdBy); // 로그인 사용자명
        log.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC)); // DB created_at 채우기

        statusLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<EquipmentStatusLogDto> getStatusLogsByEquipment(String equipmentId) {
        EquipmentEntity equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설비 ID: " + equipmentId));

        return statusLogRepository.findByEquipmentOrderByStartTimeDesc(equipment)
                .stream()
                .map(EquipmentStatusLogDto::new)
                .toList();
    }
}
