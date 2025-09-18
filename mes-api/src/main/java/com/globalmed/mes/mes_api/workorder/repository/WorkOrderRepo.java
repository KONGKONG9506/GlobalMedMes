// src/main/java/com/globalmed/mes/mes_api/workorder/WorkOrderRepo.java
package com.globalmed.mes.mes_api.workorder.repository;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface WorkOrderRepo extends JpaRepository<WorkOrderEntity, String>,
        JpaSpecificationExecutor<WorkOrderEntity> {

    // 상세보기를 위한 단일 WorkOrder 조회 시 N+1 방지 쿼리
    Optional<WorkOrderEntity> findByWorkOrderNumber(String workOrderNumber);
    @Query("SELECT wo FROM WorkOrderEntity wo " +
            "JOIN FETCH wo.item " +
            "JOIN FETCH wo.process " +
            "JOIN FETCH wo.equipment e " +
            "JOIN FETCH e.workcenter " +
            "JOIN FETCH wo.statusCode " +
            "WHERE wo.workOrderId = :workOrderId")
    Optional<WorkOrderEntity> findByIdWithDetails(String workOrderId);

    // 리스트 조회를 위한 N+1 방지 쿼리 (Specification과 함께 사용하기 위해 필요)
    @Override
    @Query("SELECT wo FROM WorkOrderEntity wo " +
            "JOIN FETCH wo.item " +
            "JOIN FETCH wo.process " +
            "JOIN FETCH wo.equipment e " +
            "JOIN FETCH e.workcenter " +
            "JOIN FETCH wo.statusCode")
    List<WorkOrderEntity> findAll(Specification<WorkOrderEntity> spec);
}