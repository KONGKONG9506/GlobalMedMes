package com.globalmed.mes.mes_api.equipstatus.repository;

import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentStatusLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EquipmentStatusRepo extends JpaRepository<EquipmentStatusLogEntity, Long> {
    Page<EquipmentStatusLogEntity> findByEquipmentIdOrderByStartTimeDesc(String equipmentId, Pageable pageable);
    Page<EquipmentStatusLogEntity> findByEquipmentIdAndStartTimeBetweenOrderByStartTimeDesc(
            String equipmentId, LocalDateTime from, LocalDateTime to, Pageable pageable);


    @Query("SELECT e FROM EquipmentStatusLogEntity e " +
            "JOIN e.statusCode c " +
            "WHERE e.equipmentId = :equipmentId " +
            "AND e.endTime IS NULL " +
            "AND c.code IN ('DOWN','IDLE') " +
            "ORDER BY e.startTime DESC")
    Optional<EquipmentStatusLogEntity> findLatestInactiveStatus(String equipmentId);
}