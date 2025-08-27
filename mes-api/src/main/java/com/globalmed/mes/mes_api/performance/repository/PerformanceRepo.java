package com.globalmed.mes.mes_api.performance.repository;

import com.globalmed.mes.mes_api.performance.domain.ProductionPerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PerformanceRepo extends JpaRepository<ProductionPerformanceEntity, Long>,
        JpaSpecificationExecutor<ProductionPerformanceEntity> {
    List<ProductionPerformanceEntity> findByEquipmentIdAndStartTimeBetween(String eqp, LocalDateTime from, LocalDateTime to);

    interface PerfAgg {
        BigDecimal getProduced(); // ← get 접두어
        BigDecimal getGood();     // ← get 접두어
    }

    @Query("""
    select COALESCE(sum(pp.producedQty), 0) as produced,
           COALESCE(sum(pp.producedQty - pp.defectQty), 0) as good
      from ProductionPerformanceEntity pp
     where pp.equipmentId = :eqp
       and pp.startTime >= :fromTs
       and pp.startTime < :toTs
    """)
    PerfAgg aggregateForDay(@Param("eqp") String equipmentId,
                            @Param("fromTs") LocalDateTime fromTs,
                            @Param("toTs") LocalDateTime toTs);
    Optional<ProductionPerformanceEntity> findByRequestId(String requestId);
}