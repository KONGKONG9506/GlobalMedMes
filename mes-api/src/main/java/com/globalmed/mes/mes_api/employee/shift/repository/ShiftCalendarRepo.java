package com.globalmed.mes.mes_api.employee.shift.repository;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftCalendarEntity;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftCalendarRepo extends JpaRepository<ShiftCalendarEntity, Long> {
    boolean existsByShiftDateAndEquipmentIdAndShift(LocalDate shiftDate, String equipmentId, ShiftEntity shift);

    // 날짜 범위 조회
    @Query("SELECT sc FROM ShiftCalendarEntity sc " +
            "WHERE (:startDate IS NULL OR sc.shiftDate >= :startDate) " +
            "AND (:endDate IS NULL OR sc.shiftDate <= :endDate) " +
            "AND (:equipmentId IS NULL OR sc.equipmentId = :equipmentId) " +
            "AND (:workcenterId IS NULL OR sc.workcenterId = :workcenterId)" +
            "ORDER BY sc.shiftDate, sc.shift.startTime")
    Page<ShiftCalendarEntity> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("equipmentId") String equipmentId,
            @Param("workcenterId") String workcenterId,
            Pageable pageable

    );
}
