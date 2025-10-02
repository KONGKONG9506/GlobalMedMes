package com.globalmed.mes.mes_api.plan.repository;

import com.globalmed.mes.mes_api.plan.domain.TbProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// PK 타입은 String, 엔티티 타입은 TbProductionPlan
@Repository
public interface ProductionPlanRepository extends JpaRepository<TbProductionPlan, String> {

    // 필요시 상태별, 기간별 조회 메소드를 추가할 수 있습니다.
    // List<TbProductionPlan> findByStatus(String status);
}