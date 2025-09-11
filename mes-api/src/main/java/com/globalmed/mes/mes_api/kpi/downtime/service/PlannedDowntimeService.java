package com.globalmed.mes.mes_api.kpi.downtime.service;

import com.globalmed.mes.mes_api.kpi.downtime.domain.PlannedDowntimeEntity;
import com.globalmed.mes.mes_api.kpi.downtime.repository.PlannedDowntimeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 계획된 다운타임(Planned Downtime) 관련 계산을 전담하는 서비스입니다.
 * PM, 교대조 휴식 시간 등 모든 계획된 다운타임 데이터를 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class PlannedDowntimeService {

    private final PlannedDowntimeRepo plannedDowntimeRepo;

    /**
     * 특정 기간 동안의 총 계획된 다운타임 시간을 분 단위로 계산합니다.
     *
     * @param equipmentId 대상 장비 ID
     * @param from        기간 시작 시간
     * @param to          기간 종료 시간
     * @return 총 계획된 다운타임 시간(분)
     */
    public long calculatePlannedDowntimeMinutes(String equipmentId, OffsetDateTime from, OffsetDateTime to) {
        long totalMinutes = 0;
        List<PlannedDowntimeEntity> plannedDowntimes = plannedDowntimeRepo.findByEquipmentAndDateRange(equipmentId, from, to);
        for (PlannedDowntimeEntity downtime : plannedDowntimes) {
            totalMinutes += downtime.getDurationMinutes();
        }
        return totalMinutes;
    }

    /**
     * 특정 기간 동안의 모든 계획된 다운타임 시간 간격 리스트를 생성합니다.
     * 이 리스트는 계획되지 않은 다운타임 계산 시 중복 시간 제거에 사용됩니다.
     *
     * @param equipmentId 대상 장비 ID
     * @param from        기간 시작 시간
     * @param to          기간 종료 시간
     * @return 계획된 다운타임 시간 간격(Interval) 리스트
     */
    public List<Interval> getPlannedDowntimeIntervals(String equipmentId, OffsetDateTime from, OffsetDateTime to) {
        List<Interval> intervals = new ArrayList<>();
        List<PlannedDowntimeEntity> plannedDowntimes = plannedDowntimeRepo.findByEquipmentAndDateRange(equipmentId, from, to);
        for (PlannedDowntimeEntity downtime : plannedDowntimes) {
            intervals.add(new Interval(downtime.getStartTime(), downtime.getEndTime()));
        }
        return intervals;
    }

    /**
     * 시간 간격을 표현하는 내부 클래스.
     * UnplannedDowntimeService에서도 사용됩니다.
     */
    public static class Interval {
        public OffsetDateTime start;
        public OffsetDateTime end;

        public Interval(OffsetDateTime start, OffsetDateTime end) {
            this.start = start;
            this.end = end;
        }
    }
}
