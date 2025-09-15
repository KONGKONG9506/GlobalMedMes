package com.globalmed.mes.mes_api.kpi.downtime.service;

import com.globalmed.mes.mes_api.production.domain.ProductionLogEntity;
import com.globalmed.mes.mes_api.production.repository.ProductionLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 계획되지 않은 다운타임(Unplanned Downtime) 관련 계산을 전담하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class UnplannedDowntimeService {
    private final ProductionLogRepo productionLogRepo;

    /**
     * ProductionLog 기록을 기반으로 계획되지 않은 비가동 시간(분)을 계산합니다.
     * DOWNTIME_END 로그의 eventValue에 기록된 시간(초)을 합산하여 총 비가동 시간을 구합니다.
     */
    public long calculateUnplannedDowntimeMinutes(String equipmentId, OffsetDateTime start, OffsetDateTime end) {
        // 1. 주어진 기간 동안 해당 장비의 'DOWNTIME_END' 로그를 조회합니다.
        // ProductionLogRepo에 findBy... 메소드가 존재한다고 가정합니다.
        List<ProductionLogEntity> downtimeEndLogs = productionLogRepo.findByEquipmentIdAndEventType_CodeAndEventTimestampBetween(
                equipmentId, "DOWNTIME_END", start.toLocalDateTime(), end.toLocalDateTime());

        // 2. 각 로그의 eventValue(초 단위)를 모두 합산합니다.
        BigDecimal totalDowntimeSeconds = downtimeEndLogs.stream()
                .map(ProductionLogEntity::getEventValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 초 단위를 분 단위로 변환하여 반환합니다.
        // 소수점 처리를 위해 BigInteger 대신 BigDecimal을 사용합니다.
        BigDecimal totalDowntimeMinutes = totalDowntimeSeconds.divide(BigDecimal.valueOf(60), RoundingMode.HALF_UP);

        return totalDowntimeMinutes.longValue();
    }
}
