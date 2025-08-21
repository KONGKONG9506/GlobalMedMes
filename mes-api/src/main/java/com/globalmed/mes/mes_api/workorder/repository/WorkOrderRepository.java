package com.globalmed.mes.mes_api.workorder.repository;

import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, String> {

    boolean existsByWorkOrderNumber(String workOrderNumber);
    Optional<WorkOrderEntity> findByWorkOrderNumber(String workOrderNumber);

}
