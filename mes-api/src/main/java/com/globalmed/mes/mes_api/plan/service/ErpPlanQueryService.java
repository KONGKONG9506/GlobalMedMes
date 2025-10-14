package com.globalmed.mes.mes_api.plan.service;

import com.globalmed.mes.mes_api.plan.domain.TbProductionPlan;
import com.globalmed.mes.mes_api.plan.repository.ProductionPlanRepository;
import com.globalmed.mes.mes_api.plan.dto.ErpPlanDetailResponseDto;
import com.globalmed.mes.mes_api.plan.dto.ErpPlanListDto;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ErpPlanQueryService {

    private final ProductionPlanRepository planRepo;
    private final WorkOrderRepo workOrderRepo;

    /**
     * 모든 Plan 목록을 조회하고 연결된 Work Order 수를 계산하여 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<ErpPlanListDto> findAllPlans() {
        List<TbProductionPlan> plans = planRepo.findAll();

        List<String> planIds = plans.stream().map(TbProductionPlan::getPlanId).toList();

        // 🚨 Work Order 카운트를 미리 조회 (N+1 문제 방지)
        // 새로 추가한 countWorkOrdersByPlanIds 메소드 사용
        List<Object[]> woCounts = workOrderRepo.countWorkOrdersByPlanIds(planIds);

        // 결과 List<Object[]>를 Map<planId, count>으로 변환
        Map<String, Long> woCountMap = woCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0], // arr[0]은 planId (String)
                        arr -> (Long) arr[1]   // arr[1]은 count (Long)
                ));

        return plans.stream()
                .map(plan -> ErpPlanListDto.fromPlanEntity(plan, woCountMap.getOrDefault(plan.getPlanId(), 0L)))
                .toList();
    }

    /**
     * 특정 Plan의 상세 정보와 연결된 모든 Work Order 상세 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public ErpPlanDetailResponseDto findPlanDetail(String planId) {
        TbProductionPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new NoSuchElementException("Plan Not Found: " + planId));

        // 연결된 Work Order 상세 정보 조회
        List<WorkOrderEntity> woEntities = workOrderRepo.findByPlanIdWithDetails(planId);

        return ErpPlanDetailResponseDto.fromPlanEntity(plan, woEntities);
    }
}