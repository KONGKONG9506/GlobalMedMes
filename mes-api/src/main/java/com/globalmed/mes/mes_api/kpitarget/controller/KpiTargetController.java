package com.globalmed.mes.mes_api.kpitarget.controller;

import com.globalmed.mes.mes_api.kpitarget.domain.KpiTargetEntity;
import com.globalmed.mes.mes_api.kpitarget.dto.KpiTargetRequestDto;
import com.globalmed.mes.mes_api.kpitarget.dto.KpiTargetResponseDto;
import com.globalmed.mes.mes_api.kpitarget.service.KpiTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kpi/actuals")
@RequiredArgsConstructor
public class KpiTargetController {

    private final KpiTargetService kpiTargetService;

    // ------------------------------
    // GET: 특정 일자, 설비 KPI 조회
    // /kpi/actuals?kpiDate=2025-08-10&equipmentId=E-0001
    // ------------------------------
    @GetMapping
    public ResponseEntity<List<KpiTargetResponseDto>> getKpiActuals(
            @RequestParam(name = "kpiDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kpiDate,
            @RequestParam(name = "equipmentId") String equipmentId
    ) {
        List<KpiTargetEntity> kpis = kpiTargetService.getKpiTargets(kpiDate, equipmentId);

        List<KpiTargetResponseDto> response = kpis.stream()
                .map(k -> KpiTargetResponseDto.builder()
                        .targetId(k.getTargetId())
                        .kpiDate(k.getKpiDate())
                        .equipmentId(k.getEquipmentId())
                        .processId(k.getProcessId())
                        .itemId(k.getItemId())
                        .targetOee(k.getTargetOee())
                        .targetYield(k.getTargetYield())
                        .targetProductivity(k.getTargetProductivity())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ------------------------------
    // POST: KPI 목표 생성/업데이트
    // ------------------------------
    @PostMapping
    public ResponseEntity<KpiTargetResponseDto> createOrUpdateKpi(
            @RequestBody KpiTargetRequestDto dto,
            Authentication authentication
    ) {
        String userId = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "system";  // 로그인 ID 또는 system
        KpiTargetEntity entity = kpiTargetService.saveOrUpdateKpiTarget(
                dto.getKpiDate(),
                dto.getEquipmentId(),
                dto.getProcessId(),
                dto.getItemId(),
                dto.getTargetOee(),
                dto.getTargetYield(),
                dto.getTargetProductivity(),
                userId
        );

        KpiTargetResponseDto response = KpiTargetResponseDto.builder()
                .targetId(entity.getTargetId())
                .kpiDate(entity.getKpiDate())
                .equipmentId(entity.getEquipmentId())
                .processId(entity.getProcessId())
                .itemId(entity.getItemId())
                .targetOee(entity.getTargetOee())
                .targetYield(entity.getTargetYield())
                .targetProductivity(entity.getTargetProductivity())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
