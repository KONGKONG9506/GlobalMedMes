package com.globalmed.mes.mes_api.kpitarget.repository;

import com.globalmed.mes.mes_api.kpitarget.domain.KpiTargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KpiTargetRepository extends JpaRepository<KpiTargetEntity, Long> {

    // 특정 일자, 설비, 공정, 품목 조회 (중복 체크용)
    Optional<KpiTargetEntity> findByKpiDateAndEquipmentIdAndProcessIdAndItemIdAndIsDeleted(
            LocalDate kpiDate,
            String equipmentId,
            String processId,
            String itemId,
            Byte isDeleted
    );

    // 특정 설비의 KPI 리스트 조회
    List<KpiTargetEntity> findByKpiDateAndEquipmentIdAndIsDeleted(
            LocalDate kpiDate,
            String equipmentId,
            Byte isDeleted
    );
}
