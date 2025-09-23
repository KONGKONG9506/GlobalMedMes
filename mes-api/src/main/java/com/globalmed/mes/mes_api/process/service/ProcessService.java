package com.globalmed.mes.mes_api.process.service;

import com.globalmed.mes.mes_api.code.dto.CertDto;
import com.globalmed.mes.mes_api.code.dto.EquipmentDto;
import com.globalmed.mes.mes_api.employee.cert.domain.ProcessCertEntity;
import com.globalmed.mes.mes_api.employee.cert.repository.ProcessCertRepo;
import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import com.globalmed.mes.mes_api.process.domain.ProcessEntity;
import com.globalmed.mes.mes_api.process.dto.ProcessDetailDto;
import com.globalmed.mes.mes_api.process.dto.ProcessListDto;
import com.globalmed.mes.mes_api.process.repository.ProcessRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepo processRepo;
    private final EquipmentRepo equipmentRepo;
    private final ProcessCertRepo proCertRepo;

    @Transactional
    public Page<ProcessListDto> getProcessList(Pageable pageable) {
        return processRepo.findAll(pageable)
                .map(entity -> new ProcessListDto(entity.getProcessId(), entity.getProcessName()));
    }

    @Transactional
    public ProcessDetailDto getProcessDetail(String processId) {
        ProcessEntity process = processRepo.findById(processId)
                .orElse(null);
        if (process == null) {
            return null;
        }

        List<EquipmentEntity> equipments = equipmentRepo.findEquipByPro(processId);
        List<ProcessCertEntity> processCerts = proCertRepo.findProCert(processId);

        List<EquipmentDto> equipmentDtos = equipments.stream()
                .map(EquipmentDto::fromEntity)
                .collect(Collectors.toList());

        List<CertDto> requiredCertDtos = processCerts.stream()
                .map(pc -> CertDto.fromEntity(pc.getCert()))
                .collect(Collectors.toList());

        OffsetDateTime lastModAt = null;
        String lastModBy = null;

        if (process.getModifiedAt() != null && process.getModifiedBy() != null) {
            lastModAt = process.getModifiedAt().atOffset(ZoneOffset.UTC);
            lastModBy = process.getModifiedBy();
        } else if (process.getCreatedAt() != null && process.getCreatedBy() != null) {
            lastModAt = process.getCreatedAt().atOffset(ZoneOffset.UTC);
            lastModBy = process.getCreatedBy();
        }

        return new ProcessDetailDto(
                process.getProcessId(),
                process.getProcessName(),
                process.getDescription(),
                equipmentDtos,
                requiredCertDtos,
                lastModAt,
                lastModBy
        );
    }
}