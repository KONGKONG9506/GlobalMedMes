package com.globalmed.mes.mes_api.kpitarget.service;

import com.globalmed.mes.mes_api.kpitarget.domain.KpiTargetEntity;
import com.globalmed.mes.mes_api.kpitarget.repository.KpiTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiTargetService {

    private final KpiTargetRepository kpiTargetRepository;

    /**
     * 특정 설비와 날짜에 대한 KPI 실제치 조회
     */
    public List<KpiTargetEntity> getKpiTargets(LocalDate kpiDate, String equipmentId) {
        return kpiTargetRepository.findByKpiDateAndEquipmentIdAndIsDeleted(kpiDate, equipmentId, (byte) 0);
    }

    /**
     * KPI 목표 생성 또는 중복 시 업데이트
     */
    @Transactional
    public KpiTargetEntity saveOrUpdateKpiTarget(LocalDate kpiDate,
                                                 String equipmentId,
                                                 String processId,
                                                 String itemId,
                                                 BigDecimal targetOee,
                                                 BigDecimal targetYield,
                                                 BigDecimal targetProductivity,
                                                 String createdBy) {

        KpiTargetEntity existing = kpiTargetRepository
                .findByKpiDateAndEquipmentIdAndProcessIdAndItemIdAndIsDeleted(
                        kpiDate, equipmentId, processId, itemId, (byte) 0
                )
                .orElse(null);

        if (existing != null) {
            // 중복이면 값 업데이트
            existing.setTargetOee(targetOee);
            existing.setTargetYield(targetYield);
            existing.setTargetProductivity(targetProductivity);
            return kpiTargetRepository.save(existing);
        }

        // 새 KPI 목표 생성
        KpiTargetEntity entity = new KpiTargetEntity();
        entity.setKpiDate(kpiDate);
        entity.setEquipmentId(equipmentId);
        entity.setProcessId(processId);
        entity.setItemId(itemId);
        entity.setTargetOee(targetOee);
        entity.setTargetYield(targetYield);
        entity.setTargetProductivity(targetProductivity);
        entity.setCreatedBy(createdBy);
        entity.setIsDeleted((byte) 0);

        return kpiTargetRepository.save(entity);
    }
}
