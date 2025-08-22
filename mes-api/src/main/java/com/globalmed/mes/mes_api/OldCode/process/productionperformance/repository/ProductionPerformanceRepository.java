package com.globalmed.mes.mes_api.OldCode.process.productionperformance.repository;

import com.globalmed.mes.mes_api.OldCode.process.productionperformance.domain.ProductionPerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionPerformanceRepository extends JpaRepository<ProductionPerformanceEntity, Long> {
    boolean existsByWorkOrderId(String workOrderId);
    List<ProductionPerformanceEntity> findAllByWorkOrderId(String workOrderId);

}
