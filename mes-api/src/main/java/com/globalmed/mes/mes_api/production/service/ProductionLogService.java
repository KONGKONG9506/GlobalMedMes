package com.globalmed.mes.mes_api.production.service;

import com.globalmed.mes.mes_api.code.CodeEntity;
import com.globalmed.mes.mes_api.code.CodeRepo;
import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentStatusLogEntity;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentStatusRepo;
import com.globalmed.mes.mes_api.production.domain.ProductionLogEntity;
import com.globalmed.mes.mes_api.production.repository.ProductionLogRepo;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.workorder.repository.WorkOrderRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
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
        ProductionLogEntity log = new ProductionLogEntity();
        log.setWorkOrderId(workOrderId);
        log.setEquipmentId(equipmentId);
        log.setProcessId(processId);
        log.setEventType(getEventCode("GOODQTY"));
        log.setEventValue(BigDecimal.valueOf(goodQty));
        log.setEventTimestamp(LocalDateTime.now());
        productionLogRepository.save(log);
    }

    /* 불량 기록 */
    public void logDefect(String workOrderId, String equipmentId, String processId, int defectQty) {
        ProductionLogEntity log = new ProductionLogEntity();
        log.setWorkOrderId(workOrderId);
        log.setEquipmentId(equipmentId);
        log.setProcessId(processId);
        log.setEventType(getEventCode("DEFECTQTY"));
        log.setEventValue(BigDecimal.valueOf(defectQty));
        log.setEventTimestamp(LocalDateTime.now());
        productionLogRepository.save(log);
    }

    /* 시작 기록 */
    public void logStart(String workOrderId, String equipmentId, String processId) {
        ProductionLogEntity log = new ProductionLogEntity();
        log.setWorkOrderId(workOrderId);
        log.setEquipmentId(equipmentId);
        log.setProcessId(processId);
        log.setEventType(getEventCode("START"));
        log.setEventValue(BigDecimal.ZERO);
        log.setEventTimestamp(LocalDateTime.now());
        productionLogRepository.save(log);
    }

    /* 종료 기록 */
    public void logEnd(String workOrderId, String equipmentId, String processId) {
        ProductionLogEntity log = new ProductionLogEntity();
        log.setWorkOrderId(workOrderId);
        log.setEquipmentId(equipmentId);
        log.setProcessId(processId);
        log.setEventType(getEventCode("END"));
        log.setEventValue(BigDecimal.ZERO);
        log.setEventTimestamp(LocalDateTime.now());
        productionLogRepository.save(log);
    }

    @Transactional
    public void logDowntime(String equipmentId, String newStatusCodeStr) {
        // 새로운 상태 코드
        CodeEntity newStatusCode = getEquipmentStatusCode(newStatusCodeStr);

        // JPQL로 endTime이 NULL인 마지막 DOWN/IDLE 상태 조회
        Optional<EquipmentStatusLogEntity> lastInactiveOpt = statusRepo.findLatestInactiveStatus(equipmentId);

        if (lastInactiveOpt.isPresent() && newStatusCode.getCode().equals("RUN")) {
            EquipmentStatusLogEntity lastStatus = lastInactiveOpt.get();

            // 진행 중 워크오더 조회
            Optional<WorkOrderEntity> currentWO = workOrderRepo.findInProgressByEquipmentId(equipmentId)
                    .stream().findFirst();

            // workOrderId, processId 추출 (없으면 null 처리 가능)
            String workOrderId = currentWO.map(WorkOrderEntity::getWorkOrderId).orElse(null);
            String processId = currentWO.map(WorkOrderEntity::getProcessId).orElse(null);

            // downtime 계산
            long downtimeMinutes = Duration.between(
                    lastStatus.getStartTime(),
                    LocalDateTime.now()
            ).toMinutes();

            // ProductionLog에 downtime 기록
            ProductionLogEntity downtimeLog = new ProductionLogEntity();
            downtimeLog.setWorkOrderId(workOrderId);
            downtimeLog.setEquipmentId(equipmentId);
            downtimeLog.setProcessId(processId);
            downtimeLog.setEventType(getEventCode("DOWNTIME"));
            downtimeLog.setEventValue(BigDecimal.valueOf(downtimeMinutes));
            downtimeLog.setEventTimestamp(LocalDateTime.now());
            productionLogRepository.save(downtimeLog);

            // 이전 장비 상태 종료 시간만 업데이트
            lastStatus.setEndTime(LocalDateTime.now());
            statusRepo.save(lastStatus);
        }
    }
}
