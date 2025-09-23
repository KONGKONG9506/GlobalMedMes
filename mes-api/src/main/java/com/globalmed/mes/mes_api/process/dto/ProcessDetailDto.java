package com.globalmed.mes.mes_api.process.dto;

import com.globalmed.mes.mes_api.code.dto.CertDto;
import com.globalmed.mes.mes_api.code.dto.EquipmentDto;

import java.time.OffsetDateTime;
import java.util.List;

// 공정 상세정보 조회용 DTO
public record ProcessDetailDto(
        String id,
        String name,
        String description,
        List<EquipmentDto> equipments,
        List<CertDto> requiredCerts,
        OffsetDateTime LastmodAt,
        String LastmodBy
) {}