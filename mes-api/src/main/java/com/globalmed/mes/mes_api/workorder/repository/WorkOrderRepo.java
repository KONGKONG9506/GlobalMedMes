// src/main/java/com/globalmed/mes/mes_api/workorder/WorkOrderRepo.java
package com.globalmed.mes.mes_api.workorder.repository;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepo extends JpaRepository<WorkOrderEntity, String>,
        JpaSpecificationExecutor<WorkOrderEntity> {
    Optional<WorkOrderEntity> findFirstByEquipmentId_EquipmentIdOrderByCreatedAtDesc(String equipmentId);

    // 상세보기를 위한 단일 WorkOrder 조회 시 N+1 방지 쿼리
    Optional<WorkOrderEntity> findByWorkOrderNumber(String workOrderNumber);
    // 상세보기를 위한 단일 WorkOrder 조회 시 N+1 방지 쿼리
    @Query("SELECT wo FROM WorkOrderEntity wo " +
            "JOIN FETCH wo.itemId " +
            "JOIN FETCH wo.processId " +
            "JOIN FETCH wo.equipmentId e " +
            "JOIN FETCH e.workcenter " +
            "JOIN FETCH wo.statusCode " +
            "WHERE wo.workOrderId = :workOrderId")
    Optional<WorkOrderEntity> findByIdWithDetails(String workOrderId);

    // 리스트 조회를 위한 N+1 방지 쿼리 (Specification과 함께 사용하기 위해 필요)
    @Query("SELECT wo FROM WorkOrderEntity wo " +
            "JOIN FETCH wo.itemId " +
            "JOIN FETCH wo.processId " +
            "JOIN FETCH wo.equipmentId e " +
            "JOIN FETCH e.workcenter " +
            "JOIN FETCH wo.statusCode")
    List<WorkOrderEntity> findAllwithDetails();

    @Query("SELECT wo.planId, COUNT(wo) FROM WorkOrderEntity wo WHERE wo.planId IN :planIds GROUP BY wo.planId")
    List<Object[]> countWorkOrdersByPlanIds(@Param("planIds") List<String> planIds);

    /**
     * 특정 Plan ID에 연결된 모든 Work Order를 상세 정보(FETCH JOIN)와 함께 조회합니다.
     */
    @Query("SELECT wo FROM WorkOrderEntity wo " +
            "JOIN FETCH wo.itemId " +
            "JOIN FETCH wo.processId " +
            "JOIN FETCH wo.equipmentId e " +
            "JOIN FETCH e.workcenter " +
            "JOIN FETCH wo.statusCode " +
            "WHERE wo.planId = :planId")
    List<WorkOrderEntity> findByPlanIdWithDetails(@Param("planId") String planId);
}