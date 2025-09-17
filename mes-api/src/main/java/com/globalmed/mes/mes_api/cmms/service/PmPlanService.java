package com.globalmed.mes.mes_api.cmms.service;

import com.globalmed.mes.mes_api.cmms.domain.CmmsPmPlan;
import com.globalmed.mes.mes_api.cmms.dto.PmPlanDto;
import com.globalmed.mes.mes_api.cmms.mapper.CmmsMapper;
import com.globalmed.mes.mes_api.cmms.repository.CmmsPmPlanRepo;
import com.globalmed.mes.mes_api.code.CodeService;
import com.globalmed.mes.mes_api.kpi.downtime.service.PMPlanDowntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PmPlanService {
    private final CmmsPmPlanRepo repo;
    private final CodeService codes;
    private final PMPlanDowntimeService pmPlanDowntimeService;
    private static final String G_CYCLE = "CYCLE_TYPE";

    @Transactional
    public PmPlanDto.Res create(PmPlanDto.CreateReq req, String actorUserId){
        var e = new CmmsPmPlan();
        e.setEquipmentId(req.equipmentId());
        e.setTaskName(req.taskName());
        e.setCycleTypeCodeId(req.cycleTypeCodeId());
        e.setCycleValue(req.cycleValue());
        e.setLastDoneAt(req.lastDoneAt());
        e.setEstimatedTakeTime(req.estimatedTakeTime());

        if (req.nextDueAt() != null) {
            e.setNextDueAt(req.nextDueAt());
        } else {
            OffsetDateTime base = (req.lastDoneAt() != null) ? req.lastDoneAt() : OffsetDateTime.now();
            String cycleCode = codes.codeOf(G_CYCLE, req.cycleTypeCodeId());
            e.setNextDueAt(computeNext(base, cycleCode, req.cycleValue()));
        }

        e.setStatus("ACTIVE");
        e.setCreatedBy(actorUserId);

        CmmsPmPlan savedPmPlan = repo.save(e);
        pmPlanDowntimeService.createPlannedDowntimeFromPmPlan(
                savedPmPlan.getEquipmentId(),
                savedPmPlan.getNextDueAt(),
                savedPmPlan.getEstimatedTakeTime(),
                savedPmPlan.getTaskName()
        );


        return CmmsMapper.toRes(repo.save(e));
    }

    @Transactional
    public PmPlanDto.Res markDoneAndRoll(Long planId, OffsetDateTime doneAt, String actorUserId){
        var plan = repo.findByIdAndDeletedFalse(planId).orElseThrow();
        plan.setLastDoneAt(doneAt);

        String cycleCode = codes.codeOf(G_CYCLE, plan.getCycleTypeCodeId());
        plan.setNextDueAt(computeNext(doneAt, cycleCode, plan.getCycleValue()));
        plan.setModifiedBy(actorUserId);

        pmPlanDowntimeService.createPlannedDowntimeFromPmPlan(
                plan.getEquipmentId(),
                plan.getNextDueAt(),
                plan.getEstimatedTakeTime(),
                plan.getTaskName()
        );

        return CmmsMapper.toRes(plan);
    }

    public Page<PmPlanDto.Res> findDue(OffsetDateTime to, String equipmentId, Pageable pageable){
        return repo.findDue(to, equipmentId, pageable).map(CmmsMapper::toRes);
    }

    private OffsetDateTime computeNext(OffsetDateTime base, String cycleCode, int cycleValue) {
        switch (cycleCode) {
            case "HOURS":    return base.plusHours(cycleValue);
            case "DAYS":     return base.plusDays(cycleValue);
            case "CALENDAR": return base.plusDays(cycleValue); // 월주기 원하면 plusMonths로 교체
            default: throw new IllegalArgumentException("Unknown cycle code: " + cycleCode);
        }
    }
}