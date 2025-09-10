package com.globalmed.mes.mes_api.cmms.service;

import com.globalmed.mes.mes_api.cmms.domain.CmmsFaultLog;
import com.globalmed.mes.mes_api.cmms.dto.FaultLogDto;
import com.globalmed.mes.mes_api.cmms.mapper.CmmsMapper;
import com.globalmed.mes.mes_api.cmms.repository.CmmsFaultLogRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class FaultLogService {
    private final CmmsFaultLogRepo repo;

    @Transactional
    public FaultLogDto.Res create(FaultLogDto.CreateReq req, String actorUserId){
        var e = new CmmsFaultLog();
        e.setEquipmentId(req.equipmentId());
        e.setLossCategoryCodeId(req.lossCategoryCodeId());
        e.setSymptom(req.symptom());
        e.setAction(req.action());
        e.setOccurredAt(req.occurredAt());
        e.setResolvedAt(req.resolvedAt());
        e.setWorkOrderId(req.workOrderId());
        e.setCreatedBy(actorUserId);
        return CmmsMapper.toRes(repo.save(e));
    }

    public Page<FaultLogDto.Res> search(String equipmentId, OffsetDateTime from, OffsetDateTime to, Pageable pageable){

        // 기본 기간: 최근 30일
        OffsetDateTime now = OffsetDateTime.now();
        if (to == null) to = now;
        if (from == null) from = to.minusDays(30);

        return repo.search(equipmentId, from, to, pageable).map(CmmsMapper::toRes);
    }
}
