package com.globalmed.mes.mes_api.kpi.downtime.service;

import com.globalmed.mes.mes_api.cmms.domain.CmmsWorkOrder;
import com.globalmed.mes.mes_api.cmms.repository.CmmsWorkOrderRepo;
import com.globalmed.mes.mes_api.code.CodeRepo;
import com.globalmed.mes.mes_api.kpi.downtime.service.PlannedDowntimeService.Interval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 계획되지 않은 다운타임(Unplanned Downtime) 관련 계산을 전담하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class UnplannedDowntimeService {

    private final CmmsWorkOrderRepo woRepo;
    private final CodeRepo codeRepo;
    private final PlannedDowntimeService plannedDowntimeService;

    private static final String G_WO_STATUS = "CMMS_WO_STATUS";
    private static final String S_DONE = "DONE";

    /**
     * 특정 기간 동안의 총 계획되지 않은 다운타임 시간을 분 단위로 계산합니다.
     * 계획된 다운타임과 중복되는 시간을 제거합니다.
     *
     * @param equipmentId 대상 장비 ID
     * @param from        기간 시작 시간
     * @param to          기간 종료 시간
     * @return 총 계획되지 않은 다운타임 시간(분)
     */
    public long calculateUnplannedDowntimeMinutes(String equipmentId, OffsetDateTime from, OffsetDateTime to) {
        var doneStatusId = codeRepo.findByGroupCodeAndCode(G_WO_STATUS, S_DONE)
                .orElseThrow(() -> new IllegalStateException("Code not found: " + G_WO_STATUS + "/" + S_DONE)).getCodeId();
        var completedWOs = woRepo.findByEquipmentIdAndStatusCodeIdAndFinishedAtBetween(
                equipmentId, doneStatusId, from, to
        );

        long totalUnplannedMinutes = 0;
        List<Interval> plannedIntervals = plannedDowntimeService.getPlannedDowntimeIntervals(equipmentId, from, to);

        for (CmmsWorkOrder wo : completedWOs) {
            if (wo.getStartedAt() != null && wo.getFinishedAt() != null) {
                Interval unplannedInterval = new Interval(wo.getStartedAt(), wo.getFinishedAt());
                long unplannedMinutesForWo = Duration.between(unplannedInterval.start, unplannedInterval.end).toMinutes();

                long overlapMinutes = 0;
                for (Interval plannedInterval : plannedIntervals) {
                    overlapMinutes += calculateOverlapMinutes(unplannedInterval, plannedInterval);
                }

                totalUnplannedMinutes += Math.max(0, unplannedMinutesForWo - overlapMinutes);
            }
        }
        return totalUnplannedMinutes;
    }

    /**
     * 두 시간 간격의 겹치는 시간을 계산합니다.
     *
     * @param i1 첫 번째 시간 간격
     * @param i2 두 번째 시간 간격
     * @return 겹치는 시간(분)
     */
    private long calculateOverlapMinutes(Interval i1, Interval i2) {
        OffsetDateTime overlapStart = i1.start.isAfter(i2.start) ? i1.start : i2.start;
        OffsetDateTime overlapEnd = i1.end.isBefore(i2.end) ? i1.end : i2.end;

        if (overlapStart.isBefore(overlapEnd)) {
            return Duration.between(overlapStart, overlapEnd).toMinutes();
        }
        return 0;
    }
}
