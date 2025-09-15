package com.globalmed.mes.mes_api.cmms.service;

import com.globalmed.mes.mes_api.cmms.domain.CmmsPmPlan;
import com.globalmed.mes.mes_api.cmms.dto.PmPlanDto;
import com.globalmed.mes.mes_api.cmms.mapper.CmmsMapper;
import com.globalmed.mes.mes_api.cmms.repository.CmmsPmPlanRepo;
import com.globalmed.mes.mes_api.code.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PmPlanService {

    private final CmmsPmPlanRepo repo;
    private final CodeService codes;

    private static final String G_CYCLE = "CYCLE_TYPE";

    @Transactional
    public PmPlanDto.Res create(PmPlanDto.CreateReq req, String actorUserId){

        OffsetDateTime nextDueAt;
        if (req.getNextDueAt() != null) {
            nextDueAt = req.getNextDueAt();
        } else {
            OffsetDateTime base = (req.getLastDoneAt() != null) ? req.getLastDoneAt() : OffsetDateTime.now();
            String cycleCode = codes.codeOf(G_CYCLE, req.getCycleTypeCodeId());
            nextDueAt = computeNext(base, cycleCode, req.getCycleValue());
        }

        CmmsPmPlan e = CmmsPmPlan.builder()
        .equipmentId(req.getEquipmentId())
        .taskName(req.getTaskName())
        .cycleTypeCodeId(req.getCycleTypeCodeId())
        .cycleValue(req.getCycleValue())
        .lastDoneAt(req.getLastDoneAt())
        .status("ACTIVE")
        .build();

        if(actorUserId != null){
            e.setCreatedBy(actorUserId);
        }

        CmmsPmPlan saved = repo.save(e);
        return CmmsMapper.toRes(saved);
    }

    @Transactional
    public PmPlanDto.Res markDoneAndRoll(Long planId, OffsetDateTime doneAt, String actorUserId){
        CmmsPmPlan plan = repo.findByIdAndDeletedFalse(planId).orElseThrow();
        plan.setLastDoneAt(doneAt);

        String cycleCode = codes.codeOf(G_CYCLE, plan.getCycleTypeCodeId());
        plan.setNextDueAt(computeNext(doneAt, cycleCode, plan.getCycleValue()));
        if(actorUserId != null){
            plan.setModifiedBy(actorUserId);
        }

        CmmsPmPlan saved = repo.save(plan);
        return CmmsMapper.toRes(saved);
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