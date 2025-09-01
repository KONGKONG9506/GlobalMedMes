package com.globalmed.mes.mes_api.kpi.repository;

import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface KpiDataRepo extends JpaRepository<KpiDataEntity, Long> {

    // 일일 배치 KPI 조회를 위해 추가된 메서드
    @Query("SELECT k FROM KpiDataEntity k WHERE k.kpiDate = :kpiDate " +
            "AND k.equipmentId = :equipmentId AND k.processId = :processId " +
            "AND k.itemId = :itemId AND k.aggregationType = :aggregationType " +
            "AND k.batchCheck = :batchCheck")
    Optional<KpiDataEntity> findDailyBatchKpi(
            @Param("kpiDate") LocalDate kpiDate,
            @Param("equipmentId") String equipmentId,
            @Param("processId") String processId,
            @Param("itemId") String itemId,
            @Param("aggregationType") String aggregationType,
            @Param("batchCheck") String batchCheck
    );

    // 실시간 KPI를 위해 추가된 메서드
    @Query("SELECT k FROM KpiDataEntity k WHERE k.kpiDate = :kpiDate " +
            "AND k.workOrderId = :workOrderId AND k.equipmentId = :equipmentId " +
            "AND k.processId = :processId AND k.itemId = :itemId " +
            "AND k.aggregationType = :aggregationType")
    Optional<KpiDataEntity> findRealtimeKpi(
            @Param("kpiDate") LocalDate kpiDate,
            @Param("workOrderId") String workOrderId,
            @Param("equipmentId") String equipmentId,
            @Param("processId") String processId,
            @Param("itemId") String itemId,
            @Param("aggregationType") String aggregationType
    );

}
