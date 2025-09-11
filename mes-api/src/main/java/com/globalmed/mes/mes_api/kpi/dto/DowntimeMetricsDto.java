package com.globalmed.mes.mes_api.kpi.dto;


import java.time.OffsetDateTime;

/**
 * 장비의 다운타임 관련 지표를 담는 DTO(Data Transfer Object)입니다.
 * <p>
 * 이 레코드는 불변(immutable) 객체로, 계산된 지표들을 안전하게 전달하는 역할을 합니다.
 *
 * @param equipmentId 장비 ID
 * @param from         측정 시작 시간
 * @param to           측정 종료 시간
 * @param totalDowntimeMinutes 총 다운타임(계획+비계획), 단위: 분
 * @param totalUnplannedDowntimeMinutes 총 비계획 다운타임, 단위: 분
 * @param totalPlannedDowntimeMinutes 총 계획 다운타임, 단위: 분
 * @param totalFaults   총 고장 건수
 * @param mttr          MTTR (Mean Time To Repair), 단위: 분
 * @param mtbf          MTBF (Mean Time Between Failures), 단위: 분
 */
public record DowntimeMetricsDto(
        String equipmentId,
        OffsetDateTime from,
        OffsetDateTime to,
        long totalDowntimeMinutes,
        long totalUnplannedDowntimeMinutes,
        long totalPlannedDowntimeMinutes,
        long totalFaults,
        long mttr,
        long mtbf) {
}