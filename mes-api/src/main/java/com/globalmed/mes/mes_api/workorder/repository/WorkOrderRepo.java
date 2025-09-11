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

    Optional<WorkOrderEntity> findByWorkOrderNumber(String workOrderNumber);

}