package com.globalmed.mes.mes_api.employee.shift.repository;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ShiftAssignmentRepo extends JpaRepository<ShiftAssignmentEntity, Long> {
    @Query("""
        SELECT sa
        FROM ShiftAssignmentEntity sa
        WHERE sa.equipmentId = :equipmentId
          AND :now BETWEEN sa.startTs AND sa.endTs
        """)
    Optional<ShiftAssignmentEntity> findCurrentWorker(
            @Param("equipmentId") String equipmentId,
            @Param("now") OffsetDateTime now
    );
}
