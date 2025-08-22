package com.globalmed.mes.mes_api.OldCode.process.productionperformance.service;

import com.globalmed.mes.mes_api.OldCode.equipment.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.OldCode.equipment.repository.EquipmentRepository;
import com.globalmed.mes.mes_api.OldCode.process.domain.ProcessEntity;
import com.globalmed.mes.mes_api.OldCode.process.productionperformance.dto.ProductionPerformanceListResponseDto;
import com.globalmed.mes.mes_api.OldCode.process.repository.ProcessRepository;
import com.globalmed.mes.mes_api.OldCode.process.productionperformance.domain.ProductionPerformanceEntity;
import com.globalmed.mes.mes_api.OldCode.process.productionperformance.dto.ProductionPerformanceRequestDto;
import com.globalmed.mes.mes_api.OldCode.process.productionperformance.dto.ProductionPerformanceResponseDto;
import com.globalmed.mes.mes_api.OldCode.process.productionperformance.repository.ProductionPerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionPerformanceService {

    private final ProductionPerformanceRepository performanceRepository;
    private final ProcessRepository processRepository;
    private final EquipmentRepository equipmentRepository;
    //목록 조회 서비스
    @Transactional(readOnly = true)
    public List<ProductionPerformanceListResponseDto> getAllPerformances() {
        return performanceRepository.findAll().stream()
                .map(p -> new ProductionPerformanceListResponseDto(
//      일부만 보낼꺼면 보내지 않을 항목을 아래와 ListDto에서 삭제
                        p.getPerformanceId(),
                        p.getWorkOrderId(),
                        p.getItemId(),
                        p.getProcess().getProcessId(),
                        p.getEquipment().getEquipmentId(),
                        p.getProducedQty(),
                        p.getDefectQty()
                ))
                .toList();
    }

    @Transactional
    public ProductionPerformanceResponseDto createPerformance(
            ProductionPerformanceRequestDto dto, String userId) {

        // 이미 같은 workOrderId가 존재하는지 체크
        if (performanceRepository.existsByWorkOrderId(dto.getWorkOrderId())) {
            throw new IllegalArgumentException("중복된 워크오더 아이디입니다: " + dto.getWorkOrderId());
        }

        ProcessEntity process = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공정 ID: " + dto.getProcessId()));

        EquipmentEntity equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 설비 ID: " + dto.getEquipmentId()));

        ProductionPerformanceEntity entity = new ProductionPerformanceEntity();
        entity.setWorkOrderId(dto.getWorkOrderId());
        entity.setItemId(dto.getItemId());
        entity.setProcess(process);
        entity.setEquipment(equipment);
        entity.setProducedQty(dto.getProducedQty());
        entity.setDefectQty(dto.getDefectQty() != null ? dto.getDefectQty() : BigDecimal.ZERO);
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());

        entity.setWorkerId(null);       // workerId는 null
        entity.setCreatedBy(userId);    // 로그인 유저 ID만 created_by에 넣음
        entity.setIsDeleted((byte) 0);

        ProductionPerformanceEntity saved = performanceRepository.save(entity);

        return ProductionPerformanceResponseDto.builder()
                .performanceId(saved.getPerformanceId())
                .workOrderId(saved.getWorkOrderId())
                .itemId(saved.getItemId())
                .processId(saved.getProcess().getProcessId())
                .equipmentId(saved.getEquipment().getEquipmentId())
                .producedQty(saved.getProducedQty())
                .defectQty(saved.getDefectQty())
                .goodQty(saved.getProducedQty().subtract(saved.getDefectQty()))
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .workerId(saved.getWorkerId())  // null 그대로 반환
                .build();
    }
    @Transactional(readOnly = true)
    public ProductionPerformanceResponseDto getPerformanceById(Long performanceId) {
        ProductionPerformanceEntity entity = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("performanceId: " + performanceId));
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ProductionPerformanceResponseDto> getPerformancesByWorkOrder(String workOrderId) {
        List<ProductionPerformanceEntity> list = performanceRepository.findAllByWorkOrderId(workOrderId);
        if (list.isEmpty()) throw new IllegalArgumentException(workOrderId);
        return list.stream().map(this::toDto).toList();
    }

    // Entity -> DTO 변환 헬퍼
    private ProductionPerformanceResponseDto toDto(ProductionPerformanceEntity entity) {
        return ProductionPerformanceResponseDto.builder()
                .performanceId(entity.getPerformanceId())
                .workOrderId(entity.getWorkOrderId())
                .itemId(entity.getItemId())
                .processId(entity.getProcess().getProcessId())
                .equipmentId(entity.getEquipment().getEquipmentId())
                .producedQty(entity.getProducedQty())
                .defectQty(entity.getDefectQty())
                .goodQty(entity.getProducedQty().subtract(entity.getDefectQty()))
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .workerId(entity.getWorkerId())
                .build();
    }
}
