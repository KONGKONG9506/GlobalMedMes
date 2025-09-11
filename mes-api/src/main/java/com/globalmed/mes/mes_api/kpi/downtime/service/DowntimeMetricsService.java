package com.globalmed.mes.mes_api.kpi.downtime.service;

import com.globalmed.mes.mes_api.cmms.repository.CmmsFaultLogRepo;
import com.globalmed.mes.mes_api.kpi.dto.DowntimeMetricsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * 장비의 핵심 다운타임 지표(MTTR, MTBF)를 계산하는 서비스입니다.
 * PlannedDowntimeService와 UnplannedDowntimeService를 조합하여 결과를 도출합니다.
 */
@Service
@RequiredArgsConstructor
public class DowntimeMetricsService {

    private final PlannedDowntimeService plannedDowntimeService;
    private final UnplannedDowntimeService unplannedDowntimeService;
    private final CmmsFaultLogRepo faultLogRepo;

    /**
     * 특정 기간 동안의 장비 다운타임 지표(MTTR, MTBF)를 계산합니다.
     *
     * @param equipmentId 대상 장비 ID
     * @param from        시작 시간
     * @param to          종료 시간
     * @return 계산된 지표 DTO
     */
    public DowntimeMetricsDto calculateDowntimeMetrics(String equipmentId, OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("기간 시작 및 종료 시간을 입력해야 합니다.");
        }

        // 1. 각 서비스에서 책임에 맞는 데이터를 계산
        long totalPlannedDowntimeMinutes = plannedDowntimeService.calculatePlannedDowntimeMinutes(equipmentId, from, to);
        long totalUnplannedDowntimeMinutes = unplannedDowntimeService.calculateUnplannedDowntimeMinutes(equipmentId, from, to);
        long totalFaults = faultLogRepo.countByEquipmentIdAndOccurredAtBetween(equipmentId, from, to);

        // 2. 최종 지표 계산
        long totalDowntimeMinutes = totalUnplannedDowntimeMinutes + totalPlannedDowntimeMinutes;

        long mttr = 0;
        long mtbf = 0;

        if (totalFaults > 0) {
            mttr = totalUnplannedDowntimeMinutes / totalFaults;
            long totalPeriodMinutes = Duration.between(from, to).toMinutes();
            long totalUptimeMinutes = totalPeriodMinutes - totalDowntimeMinutes;
            mtbf = totalUptimeMinutes / totalFaults;
        }

        return new DowntimeMetricsDto(
                equipmentId,
                from,
                to,
                totalDowntimeMinutes,
                totalUnplannedDowntimeMinutes,
                totalPlannedDowntimeMinutes,
                totalFaults,
                mttr,
                mtbf
        );
    }
}
