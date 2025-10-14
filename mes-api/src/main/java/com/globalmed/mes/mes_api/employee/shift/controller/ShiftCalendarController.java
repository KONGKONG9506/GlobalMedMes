package com.globalmed.mes.mes_api.employee.shift.controller;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftCalendarEntity;
import com.globalmed.mes.mes_api.employee.shift.dto.ShiftCalendarDto;
import com.globalmed.mes.mes_api.employee.shift.service.ShiftCalendarService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shifts/calendars")
@RequiredArgsConstructor
public class ShiftCalendarController {
    private final ShiftCalendarService calendarService;

    // 날짜별 교대 달력 생성
    @PostMapping("/generate")
    @PreAuthorize("@permChecker.has(authentication, '/work-orders','write') or hasAnyRole('ADMIN','OP')")
    public ResponseEntity<List<ShiftCalendarEntity>> generateCalendar(
            @RequestParam String date,
            @RequestParam String equipmentId,
            @RequestParam String workcenterId
    ) {
        LocalDate targetDate = LocalDate.parse(date); // yyyy-MM-dd 형식
        List<ShiftCalendarEntity> calendars = calendarService.generateCalendarForDateAndEquipment(
                targetDate, equipmentId, workcenterId
        );
        return ResponseEntity.ok(calendars);
    }


    @GetMapping()
    public ResponseEntity<?> getShiftCalendar(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String equipmentId,   // ID로 변경
            @RequestParam(required = false) String workcenterId,  // ID로 변경
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startTs,desc") String sort,
            HttpServletRequest req
    ) {
        LocalDate today = LocalDate.now();
        if (startDate == null) startDate = today;
        if (endDate == null) endDate = today;

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", "INVALID_DATE_RANGE",
                    "message", "startDate는 endDate 이전이어야 합니다.",
                    "path", req.getRequestURI(),
                    "method", req.getMethod()
            ));
        }

        // sort 파싱
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = sortParts[0];

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 서비스에서 DTO Page 반환
        Page<ShiftCalendarDto> calendarPage = calendarService.getCalendarByDate(startDate, endDate, equipmentId, workcenterId, pageable);

        if (calendarPage == null || calendarPage.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "code", "DATA_NOT_FOUND",
                    "message", "해당 기간의 교대 일정 데이터가 존재하지 않습니다",
                    "path", req.getRequestURI(),
                    "method", req.getMethod()
            ));
        }
        return ResponseEntity.ok(calendarPage);
    }



}

