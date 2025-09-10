package com.globalmed.mes.mes_api.cmms.repository;

import com.globalmed.mes.mes_api.cmms.domain.CmmsPmPlan;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface CmmsPmPlanRepo extends JpaRepository<CmmsPmPlan, Long> {
    @Query("""
        select p from CmmsPmPlan p
        where p.deleted=false and p.nextDueAt <= :to
        and (:equipmentId is null or p.equipmentId = :equipmentId)
    """)
    Page<CmmsPmPlan> findDue(@Param("to")OffsetDateTime to,
                             @Param("equipmentId") String equipmentId,
                             Pageable pageable
                             );

    Optional<CmmsPmPlan> findByIdAndDeletedFalse(Long id);
}
