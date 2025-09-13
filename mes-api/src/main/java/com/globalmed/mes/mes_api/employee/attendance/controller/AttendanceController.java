package com.globalmed.mes.mes_api.employee.attendance.controller;

import com.globalmed.mes.mes_api.employee.attendance.domain.AttendanceEntity;
import com.globalmed.mes.mes_api.employee.attendance.service.AttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/attendance")
@AllArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    @PostMapping
    public ResponseEntity<AttendanceEntity> createAttendance(@RequestBody AttendanceEntity attendanceEntity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
        String userId = (String) auth.getPrincipal();
        attendanceEntity.setUserId(userId); // 현재 로그인한 사용자의 ID로 설정

        AttendanceEntity savedAttendance = attendanceService.saveAttendance(attendanceEntity);
        return ResponseEntity.ok(savedAttendance);
    }
}
