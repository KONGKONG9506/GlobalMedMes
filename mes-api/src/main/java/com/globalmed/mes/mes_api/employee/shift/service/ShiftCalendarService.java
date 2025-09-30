package com.globalmed.mes.mes_api.employee.shift.service;


import com.globalmed.mes.mes_api.employee.shift.domain.ShiftCalendarEntity;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftEntity;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftCalendarDto;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftDto;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftCalendarRepo;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftRepo;
import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftCalendarService {
    private final ShiftRepo shiftRepo;
    private final ShiftCalendarRepo calendarRepo;
    private final EquipmentRepo equipmentRepo;
    @Transactional
    public List<ShiftCalendarEntity> generateCalendarForDateAndEquipment(LocalDate shiftDate, String equipmentId, String workcenterId) {
        // 1. 설비 존재 여부 확인t
        EquipmentEntity equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));
        // 2. 워크센터 매치 확인
        if (!equipment.getWorkcenter().getWorkcenterId().equals(workcenterId)) {
            throw new IllegalArgumentException("WC_MISMATCH");
        }
        List<ShiftEntity> shifts = shiftRepo.findAll();
        List<ShiftCalendarEntity> results = new ArrayList<>();

        for (ShiftEntity shift : shifts) {
            // 중복 확인
            if (calendarRepo.existsByShiftDateAndEquipmentIdAndShift(shiftDate, equipmentId, shift)) {
                throw new IllegalStateException("Date_E_WC_Overlaps");
            }
            // start/end LocalDateTime 생성
            LocalDateTime start = LocalDateTime.of(shiftDate, shift.getStartTime());
            LocalDateTime end = LocalDateTime.of(shiftDate, shift.getEndTime());

            // 만약 교대가 자정을 넘어가는 경우(예: 23:00 ~ 07:00)
            if (end.isBefore(start)) {
                end = end.plusDays(1);
            }

            ShiftCalendarEntity calendar = new ShiftCalendarEntity();

            OffsetDateTime startTz = start
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime()
                    .withOffsetSameInstant(ZoneOffset.UTC);

            OffsetDateTime endTz = end
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime()
                    .withOffsetSameInstant(ZoneOffset.UTC);


            calendar.setShiftDate(shiftDate);
            calendar.setShift(shift);
            calendar.setEquipmentId(equipmentId);
            calendar.setWorkcenterId(workcenterId);
            calendar.setStartTs(startTz); // KST 예시
            calendar.setEndTs(endTz);
            calendar.setCreatedBy("system");

            results.add(calendarRepo.save(calendar));
        }

        return results;
    }
    @Transactional
    public Page<ShiftCalendarDto> getCalendarByDate(LocalDate start, LocalDate end, String equipmentName, Pageable pageable) {

        List<String> equipmentIds = null;
        if (equipmentName != null && !equipmentName.trim().isEmpty()) {
            equipmentIds = equipmentRepo.findByEquipmentNametoEntity(equipmentName.trim());
            // 만약 검색된 설비 ID가 없다면, 결과를 비워서 반환
            if (equipmentIds.isEmpty()) {
                return new PageImpl<>(new ArrayList<>(), pageable, 0);
            }
        }


        // 1. 날짜 범위로 엔티티 조회
        List<ShiftCalendarEntity> allCalendar = calendarRepo.findByDateRange(start, end, equipmentIds);

        // 2. DTO 변환
        List<ShiftCalendarDto> dtoList = allCalendar.stream()
                .map(a -> {
                    EquipmentEntity eq = equipmentRepo.findById(a.getEquipmentId())
                            .orElseThrow(() -> new IllegalStateException("Equipment not found: " + a.getEquipmentId()));
                    LocalDateTime startLocal = a.getStartTs()
                            .atZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();
                    LocalDateTime endLocal = a.getEndTs()
                            .atZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();
                    return new ShiftCalendarDto(
                            a.getCalendarId(),
                            a.getShiftDate(),
                            a.getShift().getShiftName(),
                            a.getEquipmentId(),
                            eq.getEquipmentName(),
                            a.getWorkcenterId(),
                            eq.getWorkcenter().getWorkcenterName(),
                            startLocal,
                            endLocal
                    );
                })
                .toList();

        // 3. 페이지네이션 적용 (리스트에서 서브리스트 추출)
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), dtoList.size());
        List<ShiftCalendarDto> pageContent = startIdx > endIdx ? new ArrayList<>() : dtoList.subList(startIdx, endIdx);

        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }
}
