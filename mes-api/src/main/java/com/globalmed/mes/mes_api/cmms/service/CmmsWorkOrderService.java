package com.globalmed.mes.mes_api.cmms.service;

import com.globalmed.mes.mes_api.cmms.domain.*;
import com.globalmed.mes.mes_api.cmms.dto.*;
import com.globalmed.mes.mes_api.cmms.mapper.CmmsMapper;
import com.globalmed.mes.mes_api.cmms.repository.*;
import com.globalmed.mes.mes_api.code.CodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;


@Service
@RequiredArgsConstructor
public class CmmsWorkOrderService {
    private final CmmsWorkOrderRepo repo;
    private final CmmsWorkOrderLogRepo logRepo;
    private final CodeService codes;

    private static final String G_WO_STATUS = "CMMS_WO_STATUS";
    private static final String S_OPEN      = "OPEN";
    private static final String S_ASSIGNED  = "ASSIGNED";
    private static final String S_INPROG    = "IN_PROGRESS";
    private static final String S_DONE      = "DONE";

    @Transactional
    public CmmsWorkOrderDto.Res create(CmmsWorkOrderDto.createReq req, String actorUserId){
        if (req.requestId()!=null){
            var ex = repo.findByRequestId(req.requestId());
            if (ex.isPresent()) return CmmsMapper.toRes(ex.get());
        }
        var e = new CmmsWorkOrder();
        e.setEquipmentId(req.equipmentId());
        e.setTitle(req.title());
        e.setPriorityCodeId(req.priorityCodeId());
        e.setStatusCodeId(codes.idOf(G_WO_STATUS, S_OPEN));
        e.setRequestId(req.requestId());
        if (actorUserId != null) e.setCreatedBy(actorUserId);
        e = repo.save(e);

        log(e.getId(), null, e.getStatusCodeId(), "created", actorUserId);
        return CmmsMapper.toRes(e);
    }

    @Transactional
    public CmmsWorkOrderDto.Res assign(Long id, CmmsWorkOrderDto.AssignReq req, String actorUserId){
        var e = repo.findByIdAndDeletedFalse(id).orElseThrow();
        var idOpen = codes.idOf(G_WO_STATUS, S_OPEN);
        var idAssigned = codes.idOf(G_WO_STATUS, S_ASSIGNED);

        ensureCurrentIs(e.getStatusCodeId(), idOpen, "OPEN->ASSIGNED만 허용");
        var from = e.getStatusCodeId();
        e.setStatusCodeId(idAssigned);
        e.setAssigneeUserId(req.assigneeUserId());
        repo.save(e);
        log(e.getId(), from, idAssigned, "assign", actorUserId);
        return CmmsMapper.toRes(e);
    }

    @Transactional
    public CmmsWorkOrderDto.Res start(Long id, CmmsWorkOrderDto.startReq req, String actorUserId){
        var e = repo.findByIdAndDeletedFalse(id).orElseThrow();
        var idAssigned = codes.idOf(G_WO_STATUS, S_ASSIGNED);
        var idInProg   = codes.idOf(G_WO_STATUS, S_INPROG);

        ensureCurrentIs(e.getStatusCodeId(), idAssigned, "ASSIGNED->IN_PROGRESS만 허용");
        var from = e.getStatusCodeId();
        e.setStatusCodeId(idInProg);
        e.setStartedAt(req.startedAt() != null ? req.startedAt() : OffsetDateTime.now());
        repo.save(e);

        log(e.getId(), from, idInProg, "start", actorUserId);
        return CmmsMapper.toRes(e);
    }

    @Transactional
    public CmmsWorkOrderDto.Res complete(Long id, CmmsWorkOrderDto.finishReq req, String actorUserId){
        var e = repo.findByIdAndDeletedFalse(id).orElseThrow();
        var idInProg = codes.idOf(G_WO_STATUS, S_INPROG);
        var idDone   = codes.idOf(G_WO_STATUS, S_DONE);

        ensureCurrentIs(e.getStatusCodeId(), idInProg, "IN_PROGRESS->DONE만 허용");
        var from = e.getStatusCodeId();
        e.setStatusCodeId(idDone);
        e.setFinishedAt(req.finishedAt() != null ? req.finishedAt() : OffsetDateTime.now());
        if (req.actualMinutes() != null) e.setActualMinutes(req.actualMinutes());
        if (req.partsCost()     != null) e.setPartsCost(req.partsCost());
        repo.save(e);

        log(e.getId(), from, idDone, "complete", actorUserId);
        return CmmsMapper.toRes(e);
    }

    public Page<CmmsWorkOrderDto.Res> search(String status, String equipmentId, Pageable pageable){
        Long statusId = (status == null || status.isBlank()) ? null : codes.idOf(G_WO_STATUS, status);
        return repo.search(statusId, equipmentId, pageable).map(CmmsMapper::toRes);
    }

    private void ensureCurrentIs(Long currentStatusId, Long requiredCurrentId, String msgIfInvalid){
        if (currentStatusId == null || !currentStatusId.equals(requiredCurrentId)) {
            throw new IllegalStateException(msgIfInvalid);
        }
    }

    private void log(Long woId, Long fromId, Long toId, String note, String actorUserId){
        var l = new CmmsWorkOrderLog();
        l.setCmmsWoId(woId);
        l.setFromStatusCodeId(fromId);
        l.setToStatusCodeId(toId);
        l.setChangedByUserId(actorUserId);
        l.setNote(note);
        logRepo.save(l);
    }
}


