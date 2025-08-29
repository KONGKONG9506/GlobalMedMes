package com.globalmed.mes.mes_api.production.service;

import com.globalmed.mes.mes_api.code.CodeEntity;
import com.globalmed.mes.mes_api.code.CodeRepo;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentStatusRepo;
import com.globalmed.mes.mes_api.production.domain.ProductionLogEntity;
import com.globalmed.mes.mes_api.production.repository.ProductionLogRepo;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductionLogService {

    private final ProductionLogRepo productionLogRepository;
    private final CodeRepo codeRepo;
    private final EquipmentStatusRepo statusRepo;
    private final WorkOrderRepo workOrderRepo;

    private CodeEntity getEventCode(String code) {
        return codeRepo.findByGroupCodeAndCodeAndUseYn("PROD_EVENT", code, 'Y')
                .orElseThrow(() -> new IllegalArgumentException("Invalid PROD_EVENT code: " + code));
    }
    private CodeEntity getEquipmentStatusCode(String code) {
        return codeRepo.findByGroupCodeAndCodeAndUseYn("eqp_status", code, 'Y')
                .orElseThrow(() -> new IllegalArgumentException("Invalid EQP_STATUS code: " + code));
    }


    /* 양품 생산 기록 */
    public void logGood(String workOrderId, String equipmentId, String processId, int goodQty) {
        try {
            ProductionLogEntity logGood = new ProductionLogEntity();
            logGood.setWorkOrderId(workOrderId);
            logGood.setEquipmentId(equipmentId);
            logGood.setProcessId(processId);
            logGood.setEventType(getEventCode("GOODQTY"));
            logGood.setEventValue(BigDecimal.valueOf(goodQty));
            logGood.setEventTimestamp(LocalDateTime.now());
            productionLogRepository.save(logGood);
        } catch (Exception ex) {
            throw new IllegalStateException("PROD_LOG_Good_ERROR: " + ex.getMessage(), ex);
        }
    }

    /* 불량 기록 */
    public void logDefect(String workOrderId, String equipmentId, String processId, int defectQty) {
        try {
            ProductionLogEntity logDefect = new ProductionLogEntity();
            logDefect.setWorkOrderId(workOrderId);
            logDefect.setEquipmentId(equipmentId);
            logDefect.setProcessId(processId);
            logDefect.setEventType(getEventCode("DEFECTQTY"));
            logDefect.setEventValue(BigDecimal.valueOf(defectQty));
            logDefect.setEventTimestamp(LocalDateTime.now());
            productionLogRepository.save(logDefect);
        } catch (Exception ex) {
            throw new IllegalStateException("PROD_LOG_Defect_ERROR: " + ex.getMessage(), ex);
        }
    }

    /* 시작 기록 */
    public void logStart(String workOrderId, String equipmentId, String processId) {
        try {
            ProductionLogEntity logStart = new ProductionLogEntity();
            logStart.setWorkOrderId(workOrderId);
            logStart.setEquipmentId(equipmentId);
            logStart.setProcessId(processId);
            logStart.setEventType(getEventCode("START"));
            logStart.setEventValue(BigDecimal.ZERO);
            logStart.setEventTimestamp(LocalDateTime.now());
            productionLogRepository.save(logStart);
        } catch (Exception ex) {
            throw new IllegalStateException("PROD_LOG_Start_ERROR: " + ex.getMessage(), ex);
        }
    }

    /* 종료 기록 */
    public void logEnd(String workOrderId, String equipmentId, String processId) {
        try {
            ProductionLogEntity logEnd = new ProductionLogEntity();
            logEnd.setWorkOrderId(workOrderId);
            logEnd.setEquipmentId(equipmentId);
            logEnd.setProcessId(processId);
            logEnd.setEventType(getEventCode("END"));
            logEnd.setEventValue(BigDecimal.ZERO);
            logEnd.setEventTimestamp(LocalDateTime.now());
            productionLogRepository.save(logEnd);
        } catch (Exception ex) {
            throw new IllegalStateException("PROD_LOG_END_ERROR: " + ex.getMessage(), ex);
        }
    }

//    @Transactional
//    public void logDowntime(String equipmentId, String newStatusCodeStr) {
//        // 새로운 상태 코드
//        CodeEntity newStatusCode = getEquipmentStatusCode(newStatusCodeStr);
//
//        // JPQL로 endTime이 NULL인 마지막 DOWN/IDLE 상태 조회
//        Optional<EquipmentStatusLogEntity> lastInactiveOpt = statusRepo.findLatestInactiveStatus(equipmentId);
//
//        if (lastInactiveOpt.isPresent() && newStatusCode.getCode().equals("RUN")) {
//            EquipmentStatusLogEntity lastStatus = lastInactiveOpt.get();
//
//            // 진행 중 워크오더 조회
//            Optional<WorkOrderEntity> currentWO = workOrderRepo.findInProgressByEquipmentId(equipmentId)
//                    .stream().findFirst();
//
//            // workOrderId, processId 추출 (없으면 null 처리 가능)
//            String workOrderId = currentWO.map(WorkOrderEntity::getWorkOrderId).orElse(null);
//            String processId = currentWO.map(WorkOrderEntity::getProcessId).orElse(null);
//
//            // downtime 계산
//            long downtimeMinutes = Duration.between(
//                    lastStatus.getStartTime(),
//                    LocalDateTime.now()
//            ).toMinutes();
//
//            // ProductionLog에 downtime 기록
//            ProductionLogEntity downtimeLog = new ProductionLogEntity();
//            downtimeLog.setWorkOrderId(workOrderId);
//            downtimeLog.setEquipmentId(equipmentId);
//            downtimeLog.setProcessId(processId);
//            downtimeLog.setEventType(getEventCode("DOWNTIME"));
//            downtimeLog.setEventValue(BigDecimal.valueOf(downtimeMinutes));
//            downtimeLog.setEventTimestamp(LocalDateTime.now());
//            productionLogRepository.save(downtimeLog);
//
//            // 이전 장비 상태 종료 시간만 업데이트
//            lastStatus.setEndTime(LocalDateTime.now());
//            statusRepo.save(lastStatus);
//        }
//    }


}
